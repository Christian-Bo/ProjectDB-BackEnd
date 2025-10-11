package com.nexttechstore.nexttech_backend.repository.sql;

import com.nexttechstore.nexttech_backend.dto.VentaDetalleDto;
import com.nexttechstore.nexttech_backend.dto.VentaDto;
import com.nexttechstore.nexttech_backend.dto.VentaResumenDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VentasSqlRepository {

    private final JdbcTemplate jdbc;

    public VentasSqlRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /* =========================
       LISTADO (con JOIN cliente)
       ========================= */
    public List<VentaResumenDto> buscarVentas(LocalDate desde,
                                              LocalDate hasta,
                                              Integer clienteId,
                                              String numeroVenta,
                                              int limit,
                                              int offset) {
        StringBuilder sql = new StringBuilder("""
            SELECT v.id,
                   v.numero_venta,
                   v.fecha_venta,
                   v.total,
                   v.cliente_id,
                   v.estado,
                   v.tipo_pago,
                   c.nombre AS cliente_nombre
            FROM dbo.ventas v
            JOIN dbo.clientes c ON c.id = v.cliente_id
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();

        if (desde != null) {
            sql.append(" AND v.fecha_venta >= ? ");
            params.add(desde);
        }
        if (hasta != null) {
            sql.append(" AND v.fecha_venta <= ? ");
            params.add(hasta);
        }
        if (clienteId != null) {
            sql.append(" AND v.cliente_id = ? ");
            params.add(clienteId);
        }
        if (numeroVenta != null && !numeroVenta.isBlank()) {
            sql.append(" AND v.numero_venta LIKE ? ");
            params.add("%" + numeroVenta.trim() + "%");
        }

        sql.append(" ORDER BY v.fecha_venta DESC, v.id DESC ");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        params.add(offset);
        params.add(limit);

        return jdbc.query(sql.toString(), (rs, i) -> mapResumen(rs), params.toArray());
    }

    private VentaResumenDto mapResumen(ResultSet rs) throws SQLException {
        VentaResumenDto r = new VentaResumenDto();
        r.setId(rs.getInt("id"));
        r.setNumeroVenta(rs.getString("numero_venta"));
        r.setFechaVenta(rs.getDate("fecha_venta").toLocalDate());
        r.setTotal(rs.getBigDecimal("total"));
        r.setClienteId(rs.getInt("cliente_id"));
        r.setClienteNombre(rs.getString("cliente_nombre"));
        r.setEstado(rs.getString("estado"));
        r.setTipoPago(rs.getString("tipo_pago"));
        return r;
    }

    /* =========================
       MAESTRO por ID (con JOIN)
       ========================= */
    public VentaDto findVentaById(int id) {
        String sql = """
            SELECT v.id,
                   v.numero_venta,
                   v.fecha_venta,
                   v.subtotal,
                   v.descuento_general,
                   v.iva,
                   v.total,
                   v.estado,
                   v.tipo_pago,
                   COALESCE(v.observaciones,'') AS observaciones,
                   v.cliente_id,
                   c.nombre AS cliente_nombre,
                   v.vendedor_id,
                   v.cajero_id,
                   v.bodega_origen_id,
                   v.fecha_creacion
            FROM dbo.ventas v
            JOIN dbo.clientes c ON c.id = v.cliente_id
            WHERE v.id = ?
            """;
        try {
            return jdbc.queryForObject(sql, (rs, i) -> mapVenta(rs), id);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private VentaDto mapVenta(ResultSet rs) throws SQLException {
        VentaDto v = new VentaDto();
        v.setId(rs.getInt("id"));
        v.setNumeroVenta(rs.getString("numero_venta"));
        v.setFechaVenta(rs.getDate("fecha_venta").toLocalDate());
        v.setSubtotal(rs.getBigDecimal("subtotal"));
        v.setDescuentoGeneral(rs.getBigDecimal("descuento_general"));
        v.setIva(rs.getBigDecimal("iva"));
        v.setTotal(rs.getBigDecimal("total"));
        v.setEstado(rs.getString("estado"));
        v.setTipoPago(rs.getString("tipo_pago"));
        v.setObservaciones(rs.getString("observaciones")); // ya viene coalesce a ""
        v.setClienteId(rs.getInt("cliente_id"));
        v.setClienteNombre(rs.getString("cliente_nombre"));
        v.setVendedorId((Integer) rs.getObject("vendedor_id"));
        v.setCajeroId((Integer) rs.getObject("cajero_id"));
        v.setBodegaOrigenId((Integer) rs.getObject("bodega_origen_id"));

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        v.setFechaCreacion(ts != null ? ts.toInstant().atOffset(ZoneOffset.UTC) : null);

        return v;
    }

    /* =========================
       DETALLE por venta_id
       ========================= */
    public List<VentaDetalleDto> findItemsByVentaId(int ventaId) {
        String sql = """
            SELECT dv.id,
                   dv.producto_id,
                   dv.cantidad,
                   dv.precio_unitario,
                   dv.descuento_linea,
                   dv.subtotal,
                   COALESCE(dv.lote,'S/N') AS lote,
                   dv.fecha_vencimiento
            FROM dbo.detalle_ventas dv
            WHERE dv.venta_id = ?
            ORDER BY dv.id
            """;

        return jdbc.query(sql, (rs, i) -> {
            VentaDetalleDto it = new VentaDetalleDto();
            it.setId(rs.getInt("id"));
            it.setProductoId(rs.getInt("producto_id"));
            it.setCantidad(rs.getInt("cantidad")); // si tu DTO lo maneja como BigDecimal, cambia a getBigDecimal
            it.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
            it.setDescuentoLinea(rs.getBigDecimal("descuento_linea"));
            it.setSubtotal(rs.getBigDecimal("subtotal"));
            it.setLote(rs.getString("lote")); // ya coalesce a "S/N"
            var fv = rs.getDate("fecha_vencimiento");
            it.setFechaVencimiento(fv != null ? fv.toLocalDate() : null);
            return it;
        }, ventaId);
    }
}
