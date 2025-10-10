package com.nexttechstore.nexttech_backend.repository.sql;

import com.nexttechstore.nexttech_backend.dto.VentaItemDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import com.nexttechstore.nexttech_backend.exception.StockInsuficienteException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;

@Repository
public class VentasCommandRepository {

    private final JdbcTemplate jdbc;
    private final InventarioRepository inventarioRepo;

    public VentasCommandRepository(JdbcTemplate jdbc, InventarioRepository inventarioRepo) {
        this.jdbc = jdbc;
        this.inventarioRepo = inventarioRepo;
    }

    /**
     * Registra una venta (maestro + detalle), descuenta inventario y registra pago o CxC.
     * Todo en una transacción: si algo falla, se revierte.
     */
    @Transactional(rollbackFor = Exception.class)
    public int registrarVenta(VentaRequestDto req) throws DataAccessException {

        // =========================
        // 0) Calcular totales
        // =========================
        BigDecimal subtotalCalc = BigDecimal.ZERO;
        BigDecimal impuestosCalc = BigDecimal.ZERO;

        for (VentaItemDto it : req.getItems()) {
            BigDecimal cant = it.getCantidad();
            BigDecimal pu   = it.getPrecioUnitario();
            BigDecimal desc = it.getDescuento() != null ? it.getDescuento() : BigDecimal.ZERO;

            BigDecimal linea = cant.multiply(pu).subtract(desc);
            subtotalCalc = subtotalCalc.add(linea);

            if (it.getImpuesto() != null) {
                impuestosCalc = impuestosCalc.add(it.getImpuesto());
            }
        }

        // Si más adelante quieres calcular 12% sobre el subtotal:
        // impuestosCalc = impuestosCalc.add(subtotalCalc.multiply(new BigDecimal("0.12")));

        BigDecimal descuentoGeneral = BigDecimal.ZERO;
        BigDecimal iva = impuestosCalc;                 // mapeamos impuesto de ítems a IVA del encabezado
        BigDecimal total = subtotalCalc.subtract(descuentoGeneral).add(iva);

        // =========================
        // 1) Generar correlativo: V-000X
        // =========================
        String nextNumSql = """
            SELECT RIGHT('0000' + CAST(ISNULL(MAX(CAST(SUBSTRING(numero_venta, 3, 10) AS INT)), 0) + 1 AS VARCHAR(10)), 4)
            FROM dbo.ventas
            WHERE numero_venta LIKE 'V-%'
            """;
        String nextSeq = jdbc.queryForObject(nextNumSql, String.class);
        if (nextSeq == null) nextSeq = "0001";
        final String numeroVenta = "V-" + nextSeq;
        final LocalDate hoy = LocalDate.now();

        // =========================
        // 2) Insert maestro (ventas)
        // =========================
        String insertVenta = """
            INSERT INTO dbo.ventas
              (numero_venta, fecha_venta, subtotal, descuento_general, iva, total, estado, tipo_pago,
               observaciones, cliente_id, vendedor_id, cajero_id, bodega_origen_id, fecha_creacion)
            VALUES
              (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME())
            """;

        KeyHolder kh = new GeneratedKeyHolder();
        final BigDecimal subtotal = subtotalCalc;
        final BigDecimal ivaFinal = iva;
        final BigDecimal descGen  = descuentoGeneral;

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(insertVenta, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,  numeroVenta);
            ps.setDate(2,    Date.valueOf(hoy));
            ps.setBigDecimal(3, subtotal);
            ps.setBigDecimal(4, descGen);
            ps.setBigDecimal(5, ivaFinal);
            ps.setBigDecimal(6, total);
            ps.setString(7,  "P"); // Pendiente (o como definan el flujo)
            ps.setString(8,  req.getTipoPago() != null ? req.getTipoPago() : "C");
            ps.setString(9,  req.getObservaciones() != null ? req.getObservaciones() : "");
            ps.setInt(10,    req.getClienteId());
            ps.setObject(11, req.getVendedorId());     // nullable
            ps.setObject(12, req.getCajeroId());       // nullable
            ps.setObject(13, req.getBodegaOrigenId()); // nullable
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("No se obtuvo ID de la venta.");
        int ventaId = key.intValue();

        // =========================
        // 3) Detalle + inventario + movimiento
        // =========================
        String insertDet = """
            INSERT INTO dbo.detalle_ventas
              (venta_id, producto_id, cantidad, precio_unitario, descuento_linea, subtotal, lote, fecha_vencimiento)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        for (VentaItemDto it : req.getItems()) {
            BigDecimal cant = it.getCantidad();
            BigDecimal pu   = it.getPrecioUnitario();
            BigDecimal desc = it.getDescuento() != null ? it.getDescuento() : BigDecimal.ZERO;
            BigDecimal linea = cant.multiply(pu).subtract(desc);

            // 3.1 Validar inventario y descontar
            var inv = inventarioRepo.getInventario(it.getProductoId(), it.getBodegaId());
            if (inv == null) {
                throw new DataIntegrityViolationException(
                        "No existe inventario para productoId=" + it.getProductoId() + " en bodegaId=" + it.getBodegaId()
                );
            }
            var disponible = inv.cantidadActual;
            if (disponible.compareTo(cant) < 0) {
                throw new StockInsuficienteException(it.getProductoId(), it.getBodegaId(), disponible, cant);
            }
            boolean ok = inventarioRepo.descontarStock(it.getProductoId(), it.getBodegaId(), cant);
            if (!ok) {
                // carrera: revalidar y fallar
                var inv2 = inventarioRepo.getInventario(it.getProductoId(), it.getBodegaId());
                BigDecimal disp2 = (inv2 != null ? inv2.cantidadActual : BigDecimal.ZERO);
                throw new StockInsuficienteException(it.getProductoId(), it.getBodegaId(), disp2, cant);
            }
            BigDecimal nueva = disponible.subtract(cant);

            // 3.2 Insert detalle (usamos PreparedStatementSetter para setear DATE nulo correctamente)
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(insertDet);
                ps.setInt(1, ventaId);
                ps.setInt(2, it.getProductoId());
                ps.setBigDecimal(3, cant);           // cantidad DECIMAL
                ps.setBigDecimal(4, pu);
                ps.setBigDecimal(5, desc);
                ps.setBigDecimal(6, linea);
                ps.setString(7, (it.getLote() != null && !it.getLote().isBlank()) ? it.getLote() : "S/N");
                if (it.getFechaVencimiento() != null) {
                    ps.setDate(8, Date.valueOf(it.getFechaVencimiento()));
                } else {
                    ps.setNull(8, Types.DATE);
                }
                return ps;
            });

            // 3.3 Movimiento inventario (salida por venta)
            inventarioRepo.insertarMovimientoSalida(
                    it.getProductoId(),
                    it.getBodegaId(),
                    cant,
                    disponible,
                    nueva,
                    inv.ultimoCosto,
                    ventaId,
                    numeroVenta,
                    (req.getVendedorId() != null ? req.getVendedorId() : req.getUsuarioId())
            );
        }

        // =========================
        // 4) Pago contado o CxC (crédito)
        // =========================
        String tipoPago = (req.getTipoPago() != null ? req.getTipoPago() : "C");

        if ("C".equalsIgnoreCase(tipoPago)) {
            // Contado: un solo registro de pago (simple). Si luego quieres pagos múltiples, haz endpoint aparte.
            String insPago = """
                INSERT INTO dbo.ventas_pagos (venta_id, forma_pago, monto, referencia)
                VALUES (?, ?, ?, ?)
                """;
            jdbc.update(insPago, ventaId, "EFE", total, "Caja");
        } else {
            // Crédito: crear documento de CxC por el total
            String insCxC = """
                INSERT INTO dbo.cxc_documentos
                  (cliente_id, origen_tipo, origen_id, numero_documento, fecha_emision, fecha_vencimiento,
                   moneda, monto_total, saldo_pendiente, estado)
                VALUES (?, 'V', ?, ?, ?, DATEADD(day, 30, ?), 'GTQ', ?, ?, 'P')
                """;
            jdbc.update(insCxC,
                    req.getClienteId(),
                    ventaId,
                    numeroVenta,
                    Date.valueOf(hoy),
                    Date.valueOf(hoy),
                    total,
                    total
            );
        }

        return ventaId;
    }
}
