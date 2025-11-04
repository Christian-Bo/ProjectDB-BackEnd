package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.dto.facturas.FacturaDetalleDto;
import com.nexttechstore.nexttech_backend.dto.facturas.FacturaHeaderDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class FacturasQueryRepository {

    private final JdbcTemplate jdbc;

    public FacturasQueryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String,Object>> listar(LocalDate desde, LocalDate hasta, String serie, String numero, int page, int size) {
        StringBuilder sql = new StringBuilder("""
            SELECT f.id, f.serie, f.numero, CONVERT(varchar(10), f.fecha_emision, 120) AS fecha_emision,
                   f.activa, v.total, c.nombre AS cliente
            FROM facturas f
            JOIN ventas v ON v.id = f.referencia_id AND f.referencia_tipo='V'
            JOIN clientes c ON c.id = v.cliente_id
            WHERE 1=1
        """);

        if (desde != null) sql.append(" AND f.fecha_emision >= '").append(desde).append("'");
        if (hasta != null) sql.append(" AND f.fecha_emision <  '").append(hasta.plusDays(1)).append("'");
        if (serie != null && !serie.isBlank()) sql.append(" AND f.serie = '").append(serie.replace("'", "''")).append("'");
        if (numero != null && !numero.isBlank()) sql.append(" AND CAST(f.numero AS varchar(50)) LIKE '%").append(numero.replace("'", "''")).append("%'");

        sql.append(" ORDER BY f.fecha_emision DESC, f.id DESC ");
        sql.append(" OFFSET ").append(page * size).append(" ROWS FETCH NEXT ").append(size).append(" ROWS ONLY");

        return jdbc.queryForList(sql.toString());
    }

    public FacturaHeaderDto obtenerHeader(int id) {
        return jdbc.query("""
            SELECT f.id, f.serie_id, f.serie,
                   CONVERT(varchar(50), f.correlativo) AS correlativo,
                   CONVERT(varchar(50), f.numero)      AS numero,
                   CONVERT(varchar(19), f.fecha_emision, 120) AS fechaEmision,
                   f.activa,
                   v.id AS ventaId, v.total, v.subtotal, v.iva, v.descuento_general,
                   c.nombre AS cliente, c.nit, v.tipo_pago
            FROM facturas f
            JOIN ventas v ON v.id = f.referencia_id AND f.referencia_tipo='V'
            JOIN clientes c ON c.id = v.cliente_id
            WHERE f.id = ?
        """, (rs)-> rs.next() ? mapHeader(rs) : null, id);
    }

    private FacturaHeaderDto mapHeader(ResultSet rs) {
        try {
            return new FacturaHeaderDto(
                    rs.getInt("id"),
                    rs.getInt("serie_id"),
                    rs.getString("serie"),
                    rs.getString("correlativo"),
                    rs.getString("numero"),
                    rs.getString("fechaEmision"),
                    rs.getBoolean("activa"),
                    rs.getInt("ventaId"),
                    rs.getBigDecimal("subtotal"),
                    rs.getBigDecimal("descuento_general"),
                    rs.getBigDecimal("iva"),
                    rs.getBigDecimal("total"),
                    rs.getString("cliente"),
                    rs.getString("nit"),
                    rs.getString("tipo_pago")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<FacturaDetalleDto> obtenerDetallePorFactura(int facturaId) {
        return jdbc.query("""
            SELECT dv.producto_id, p.nombre AS producto,
                   dv.cantidad, dv.precio_unitario, dv.descuento_linea, dv.subtotal,
                   ISNULL(dv.lote,'S/N') lote,
                   CONVERT(varchar(10), dv.fecha_vencimiento, 120) AS vence
            FROM facturas f
            JOIN ventas v ON v.id = f.referencia_id AND f.referencia_tipo='V'
            JOIN detalle_ventas dv ON dv.venta_id = v.id
            JOIN productos p ON p.id = dv.producto_id
            WHERE f.id = ?
        """, (rs, rowNum) -> new FacturaDetalleDto(
                rs.getInt("producto_id"),
                rs.getString("producto"),
                rs.getBigDecimal("cantidad"),
                rs.getBigDecimal("precio_unitario"),
                rs.getBigDecimal("descuento_linea"),
                rs.getBigDecimal("subtotal"),
                rs.getString("lote"),
                rs.getString("vence")
        ), facturaId);
    }

    /* ====== NUEVOS MÉTODOS ====== */

    /** Devuelve la última factura (id, fel_acuse) de una venta, o null si no hay */
    public Map<String,Object> findUltimaFacturaPorVenta(int ventaId) {
        return jdbc.query("""
            SELECT TOP 1 id, fel_acuse
            FROM facturas
            WHERE referencia_tipo='V' AND referencia_id=?
            ORDER BY id DESC
        """, rs -> rs.next()
                ? Map.of("id", rs.getInt("id"), "fel_acuse", rs.getString("fel_acuse"))
                : null, ventaId);
    }

    /** Guarda snapshot empaquetado en fel_acuse */
    public void updateFelAcuse(int facturaId, String jsonPacked) {
        jdbc.update("UPDATE facturas SET fel_acuse=? WHERE id=?", jsonPacked, facturaId);
    }
}
