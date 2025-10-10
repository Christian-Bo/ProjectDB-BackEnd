package com.nexttechstore.nexttech_backend.repository.sql;

import com.nexttechstore.nexttech_backend.dto.VentaItemDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;

@Repository
public class VentasCommandRepository {

    private final JdbcTemplate jdbc;

    public VentasCommandRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int registrarVenta(VentaRequestDto req) throws DataAccessException {
        // Totales
        final BigDecimal subtotalFinal = req.getItems().stream().map(it -> {
            BigDecimal cant = new BigDecimal(it.getCantidad().toString());
            BigDecimal pu   = it.getPrecioUnitario();
            BigDecimal desc = it.getDescuento() != null ? it.getDescuento() : BigDecimal.ZERO;
            return cant.multiply(pu).subtract(desc);
        }).reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal descuentoGeneral = BigDecimal.ZERO;
        final BigDecimal iva = BigDecimal.ZERO; // si luego quieres 12%, cambia aquí
        final BigDecimal totalFinal = subtotalFinal.subtract(descuentoGeneral).add(iva);

        // Correlativo padded
        final String nextNumSql = """
            SELECT RIGHT('0000' + CAST(ISNULL(MAX(CAST(SUBSTRING(numero_venta, 3, 10) AS INT)), 0) + 1 AS VARCHAR(10)), 4)
            FROM dbo.ventas
            WHERE numero_venta LIKE 'V-%'
            """;
        String nextSeq = jdbc.queryForObject(nextNumSql, String.class);
        if (nextSeq == null) nextSeq = "0001";
        final String numeroVenta = "V-" + nextSeq;

        final LocalDate hoy = LocalDate.now();

        // Defaults inteligentes
        final Integer vendedorId     = (req.getVendedorId() != null) ? req.getVendedorId() : req.getUsuarioId();
        final Integer cajeroId       = req.getCajeroId(); // Si quieres, usa req.getUsuarioId() como default
        final Integer bodegaOrigenId = (req.getBodegaOrigenId() != null) ? req.getBodegaOrigenId() : req.getSerieId();
        final String  tipoPago       = (req.getTipoPago() != null && !req.getTipoPago().isBlank()) ? req.getTipoPago() : "C";
        final String  observaciones  = (req.getObservaciones() != null) ? req.getObservaciones() : "";

        // Insert maestro
        final String insertVenta = """
            INSERT INTO dbo.ventas
              (numero_venta, fecha_venta, subtotal, descuento_general, iva, total, estado, tipo_pago,
               observaciones, cliente_id, vendedor_id, cajero_id, bodega_origen_id, fecha_creacion)
            VALUES
              (?, ?, ?, ?, ?, ?, 'P', ?, ?, ?, ?, ?, ?, SYSUTCDATETIME())
            """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(insertVenta, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, numeroVenta);
            ps.setDate(2, Date.valueOf(hoy));
            ps.setBigDecimal(3, subtotalFinal);
            ps.setBigDecimal(4, descuentoGeneral);
            ps.setBigDecimal(5, iva);
            ps.setBigDecimal(6, totalFinal);
            ps.setString(7, tipoPago);
            ps.setString(8, observaciones);
            ps.setInt(9, req.getClienteId());
            ps.setInt(10, vendedorId);
            if (cajeroId != null) ps.setInt(11, cajeroId); else ps.setNull(11, java.sql.Types.INTEGER);
            if (bodegaOrigenId != null) ps.setInt(12, bodegaOrigenId); else ps.setNull(12, java.sql.Types.INTEGER);
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("No se obtuvo ID de la venta.");
        int ventaId = key.intValue();

        // Insert detalle
        final String insertDet = """
            INSERT INTO dbo.detalle_ventas
              (venta_id, producto_id, cantidad, precio_unitario, descuento_linea, subtotal, lote, fecha_vencimiento)
            VALUES (?,?,?,?,?,?,?,?)
            """;

        for (VentaItemDto it : req.getItems()) {
            BigDecimal cant = new BigDecimal(it.getCantidad().toString());
            BigDecimal pu   = it.getPrecioUnitario();
            BigDecimal desc = it.getDescuento() != null ? it.getDescuento() : BigDecimal.ZERO;
            BigDecimal linea = cant.multiply(pu).subtract(desc);

            final String lote = (it.getLote() != null && !it.getLote().isBlank()) ? it.getLote() : "S/N";
            java.sql.Date fv = (it.getFechaVencimiento() != null) ? java.sql.Date.valueOf(it.getFechaVencimiento()) : null;

            jdbc.update(insertDet,
                    ventaId,
                    it.getProductoId(),
                    it.getCantidad().intValue(),
                    pu,
                    desc,
                    linea,
                    lote,
                    fv
            );
        }

        return ventaId;
    }
}
