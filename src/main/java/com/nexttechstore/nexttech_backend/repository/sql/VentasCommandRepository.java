package com.nexttechstore.nexttech_backend.repository.sql;

import com.nexttechstore.nexttech_backend.dto.VentaItemDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import com.nexttechstore.nexttech_backend.exception.StockInsuficienteException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;

@Repository
public class VentasCommandRepository {

    private final JdbcTemplate jdbc;

    // Este repo es opcional (no existe si no hay inventario en la BD)
    @Nullable
    private final InventarioRepository inventarioRepo;

    // Flag desde properties para encender/apagar inventario
    private final boolean inventoryEnabled;

    public VentasCommandRepository(JdbcTemplate jdbc,
                                   @Nullable InventarioRepository inventarioRepo,
                                   @Value("${app.inventory.enabled:false}") boolean inventoryEnabled) {
        this.jdbc = jdbc;
        this.inventarioRepo = inventarioRepo;
        this.inventoryEnabled = inventoryEnabled;
    }

    public int registrarVenta(VentaRequestDto req) throws DataAccessException {
        // 0) Totales base
        BigDecimal subtotalCalc = BigDecimal.ZERO;
        for (VentaItemDto it : req.getItems()) {
            BigDecimal cant = it.getCantidad();
            BigDecimal pu   = it.getPrecioUnitario();
            BigDecimal desc = it.getDescuento() != null ? it.getDescuento() : BigDecimal.ZERO;
            subtotalCalc = subtotalCalc.add(cant.multiply(pu).subtract(desc));
        }
        BigDecimal iva = BigDecimal.ZERO;            // si luego quieres 12%: subtotalCalc.multiply(new BigDecimal("0.12"))
        BigDecimal descuentoGeneral = BigDecimal.ZERO;
        BigDecimal total = subtotalCalc.subtract(descuentoGeneral).add(iva);

        // 1) Correlativo V-000X
        String nextNumSql = """
            SELECT RIGHT('0000' + CAST(ISNULL(MAX(CAST(SUBSTRING(numero_venta, 3, 10) AS INT)), 0) + 1 AS VARCHAR(10)), 4)
            FROM dbo.ventas
            WHERE numero_venta LIKE 'V-%'
            """;
        String nextSeq = jdbc.queryForObject(nextNumSql, String.class);
        if (nextSeq == null) nextSeq = "0001";
        final String numeroVenta = "V-" + nextSeq;
        final LocalDate hoy = LocalDate.now();

        // 2) Insert maestro
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
            ps.setString(7,  "P"); // Pendiente
            ps.setString(8,  req.getTipoPago() != null ? req.getTipoPago() : "C");
            ps.setString(9,  req.getObservaciones() != null ? req.getObservaciones() : "");
            ps.setInt(10,    req.getClienteId());
            ps.setObject(11, req.getVendedorId());         // puede ser nulo
            ps.setObject(12, req.getCajeroId());           // puede ser nulo
            ps.setObject(13, req.getBodegaOrigenId());     // puede ser nulo
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("No se obtuvo ID de la venta.");
        int ventaId = key.intValue();

        // 3) Insert detalle (+ opcional inventario)
        String insertDet = """
            INSERT INTO dbo.detalle_ventas
              (venta_id, producto_id, cantidad, precio_unitario, descuento_linea, subtotal, lote, fecha_vencimiento)
            VALUES (?,?,?,?,?,?,?,?)
            """;

        for (VentaItemDto it : req.getItems()) {
            BigDecimal cant = it.getCantidad();
            BigDecimal pu   = it.getPrecioUnitario();
            BigDecimal desc = it.getDescuento() != null ? it.getDescuento() : BigDecimal.ZERO;
            BigDecimal linea = cant.multiply(pu).subtract(desc);

            // 3.1 Si el inventario está habilitado, valida y descuenta
            if (inventoryEnabled) {
                if (inventarioRepo == null) {
                    throw new IllegalStateException("Inventario habilitado pero no hay InventarioRepository configurado.");
                }
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
                    var inv2 = inventarioRepo.getInventario(it.getProductoId(), it.getBodegaId());
                    BigDecimal disp2 = (inv2 != null ? inv2.cantidadActual : BigDecimal.ZERO);
                    throw new StockInsuficienteException(it.getProductoId(), it.getBodegaId(), disp2, cant);
                }
                BigDecimal nueva = disponible.subtract(cant);

                // (opcional) movimiento
                inventarioRepo.insertarMovimientoSalida(
                        it.getProductoId(),
                        it.getBodegaId(),
                        cant,
                        disponible,
                        nueva,
                        inv.ultimoCosto,
                        ventaId,
                        numeroVenta,
                        req.getVendedorId() != null ? req.getVendedorId() : req.getUsuarioId()
                );
            }

            // 3.2 Detalle
            jdbc.update(insertDet,
                    ventaId,
                    it.getProductoId(),
                    // Si tu columna es DECIMAL, cambia a cant directamente:
                    it.getCantidad().intValue(),
                    pu,
                    desc,
                    linea,
                    it.getLote() != null && !it.getLote().isBlank() ? it.getLote() : "S/N",
                    it.getFechaVencimiento()
            );
        }

        // 4) Pagos / CxC según tipoPago
        String tipoPago = req.getTipoPago() != null ? req.getTipoPago() : "C";
        if ("C".equalsIgnoreCase(tipoPago)) {
            String insPago = """
                INSERT INTO dbo.ventas_pagos (venta_id, forma_pago, monto, referencia)
                VALUES (?, ?, ?, ?)
                """;
            jdbc.update(insPago, ventaId, "EFE", total, "Caja");
        } else {
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
