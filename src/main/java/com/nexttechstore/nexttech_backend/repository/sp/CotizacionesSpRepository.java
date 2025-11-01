package com.nexttechstore.nexttech_backend.repository.sp;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.nexttechstore.nexttech_backend.dto.CotizacionCreateItemDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Repository
public class CotizacionesSpRepository {


    private final DataSource dataSource;

    public CotizacionesSpRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /* ==================== Helpers ==================== */
    private static Integer readInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }
    private static BigDecimal readDec(ResultSet rs, String col) throws SQLException {
        try { return rs.getBigDecimal(col); } catch (SQLException e) { return null; }
    }
    private static LocalDate readDate(ResultSet rs, String col) throws SQLException {
        Date d = rs.getDate(col);
        return (d==null) ? null : d.toLocalDate();
    }
    private static String readStr(ResultSet rs, String col) throws SQLException {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }

    /* ==================== SP: LIST ==================== */
    public List<Map<String,Object>> listar(LocalDate desde, LocalDate hasta,
                                           Integer clienteId, String numero,
                                           Integer page, Integer size) throws SQLException {
        final String sql = "EXEC dbo.sp_cotizaciones_list @p_desde=?, @p_hasta=?, @p_cliente_id=?, @p_numero=?, @p_page=?, @p_size=?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (desde != null) ps.setDate(1, Date.valueOf(desde)); else ps.setNull(1, Types.DATE);
            if (hasta != null) ps.setDate(2, Date.valueOf(hasta)); else ps.setNull(2, Types.DATE);
            if (clienteId != null) ps.setInt(3, clienteId); else ps.setNull(3, Types.INTEGER);
            if (numero != null && !numero.isBlank()) ps.setString(4, numero); else ps.setNull(4, Types.NVARCHAR);
            ps.setInt(5, page != null ? page : 0);
            ps.setInt(6, size != null ? size : 50);

            List<Map<String,Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getInt("id"));
                    m.put("numero_cotizacion", readStr(rs,"numero_cotizacion"));
                    m.put("fecha_cotizacion", readDate(rs,"fecha_cotizacion"));
                    m.put("total", readDec(rs,"total"));
                    m.put("estado", readStr(rs,"estado"));
                    m.put("cliente_id", readInt(rs,"cliente_id"));
                    m.put("cliente_nombre", readStr(rs,"cliente_nombre"));
                    out.add(m);
                }
            }
            return out;
        }
    }

    /* ==================== SP: GET BY ID ==================== */
    public Map<String,Object> getById(int id) throws SQLException {
        final String sql = "EXEC dbo.sp_cotizaciones_get_by_id @p_id=?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);

            Map<String,Object> root = new HashMap<>();
            List<Map<String,Object>> detalle = new ArrayList<>();

            boolean first = true;
            boolean hasResults = ps.execute();
            while (hasResults) {
                try (ResultSet rs = ps.getResultSet()) {
                    if (first) {
                        if (rs.next()) {
                            Map<String,Object> h = new LinkedHashMap<>();
                            h.put("id", rs.getInt("id"));
                            h.put("numero_cotizacion", readStr(rs,"numero_cotizacion"));
                            h.put("fecha_cotizacion", readDate(rs,"fecha_cotizacion"));
                            h.put("fecha_vigencia", readDate(rs,"fecha_vigencia"));
                            h.put("subtotal", readDec(rs,"subtotal"));
                            h.put("descuento_general", readDec(rs,"descuento_general"));
                            h.put("iva", readDec(rs,"iva"));
                            h.put("total", readDec(rs,"total"));
                            h.put("estado", readStr(rs,"estado"));
                            h.put("observaciones", readStr(rs,"observaciones"));
                            h.put("terminos_condiciones", readStr(rs,"terminos_condiciones"));
                            h.put("cliente_id", readInt(rs,"cliente_id"));
                            h.put("cliente_codigo", readStr(rs,"cliente_codigo"));
                            h.put("cliente_nombre", readStr(rs,"cliente_nombre"));
                            h.put("vendedor_id", readInt(rs,"vendedor_id"));
                            h.put("vendedor_nombre", readStr(rs,"vendedor_nombre"));
                            root.put("header", h);
                        }
                        first = false;
                    } else {
                        while (rs.next()) {
                            Map<String,Object> d = new LinkedHashMap<>();
                            d.put("detalle_id", readInt(rs,"detalle_id"));
                            d.put("producto_id", readInt(rs,"producto_id"));
                            d.put("producto_codigo", readStr(rs,"producto_codigo"));
                            d.put("producto_nombre", readStr(rs,"producto_nombre"));
                            d.put("cantidad", readInt(rs,"cantidad"));
                            d.put("precio_unitario", readDec(rs,"precio_unitario"));
                            d.put("descuento_linea", readDec(rs,"descuento_linea"));
                            d.put("subtotal", readDec(rs,"subtotal"));
                            d.put("descripcion_adicional", readStr(rs,"descripcion_adicional"));
                            detalle.add(d);
                        }
                    }
                }
                hasResults = ps.getMoreResults();
            }
            root.put("detalle", detalle);
            return root;
        }
    }

    /* ==================== SP: CREATE (TVP con unwrap) ==================== */
    public int crear(Integer clienteId, Integer vendedorId, Date fechaVigencia,
                     String observaciones, String terminos,
                     java.math.BigDecimal descGen, java.math.BigDecimal iva,
                     List<CotizacionCreateItemDto> items) throws SQLException {

        if (items == null || items.isEmpty())
            throw new SQLException("Detalle vacío");

        // Armar TVP
        SQLServerDataTable tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("producto_id", Types.INTEGER);
        tvp.addColumnMetadata("cantidad", Types.INTEGER);
        tvp.addColumnMetadata("precio_unitario", Types.DECIMAL);
        tvp.addColumnMetadata("descuento_linea", Types.DECIMAL);
        tvp.addColumnMetadata("descripcion_adicional", Types.NVARCHAR);

        for (CotizacionCreateItemDto it : items) {
            tvp.addRow(
                    it.getProductoId(),
                    it.getCantidad(),
                    it.getPrecioUnitario(),
                    it.getDescuentoLinea(),
                    it.getDescripcionAdicional()
            );
        }

        final String sql =
                "DECLARE @id INT, @code INT, @message NVARCHAR(200); " +
                        "EXEC dbo.sp_cotizaciones_create " +
                        " @p_cliente_id=?, @p_vendedor_id=?, @p_fecha_vigencia=?, " +
                        " @p_observaciones=?, @p_terminos=?, " +
                        " @p_descuento_general=?, @p_iva=?, @p_detalle=?, " +
                        " @out_cot_id=@id OUTPUT, @out_status_code=@code OUTPUT, @out_message=@message OUTPUT; " +
                        "SELECT @id AS id, @code AS code, @message AS message;";

        try (Connection c = dataSource.getConnection()) {

            // Unwrap a SQLServerConnection para obtener un SQLServerPreparedStatement real
            SQLServerConnection sqlConn = c.unwrap(SQLServerConnection.class);

            try (SQLServerPreparedStatement ps = (SQLServerPreparedStatement) sqlConn.prepareStatement(sql)) {

                if (clienteId != null) ps.setInt(1, clienteId); else ps.setNull(1, Types.INTEGER);
                if (vendedorId != null) ps.setInt(2, vendedorId); else ps.setNull(2, Types.INTEGER);
                if (fechaVigencia != null) ps.setDate(3, fechaVigencia); else ps.setNull(3, Types.DATE);
                ps.setString(4, observaciones);
                ps.setString(5, terminos);
                ps.setBigDecimal(6, descGen == null ? java.math.BigDecimal.ZERO : descGen);
                ps.setBigDecimal(7, iva == null ? java.math.BigDecimal.ZERO : iva);

                // <- AQUÍ: TVP correcto
                ps.setStructured(8, "dbo.tvp_cotizacion_detalle_v1", tvp);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("sp_cotizaciones_create sin resultado");
                    int code = rs.getInt("code");
                    String message = rs.getString("message"); // nombre estable
                    int id = rs.getInt("id");
                    if (code != 0) throw new SQLException("sp_cotizaciones_create ("+code+"): "+message);
                    return id;
                }
            }
        }
    }

    /* ==================== SP: TO-VENTA (lectura tolerante msg/message) ==================== */
    public int convertirAVenta(int cotizacionId, int bodegaId, int serieId, Integer cajeroId) throws SQLException {
        final String sql =
                "DECLARE @id INT, @code INT, @message NVARCHAR(200); " +
                        "EXEC dbo.sp_cotizaciones_to_venta " +
                        " @p_cotizacion_id=?, @p_bodega_origen_id=?, @p_serie_id=?, @p_cajero_id=?, " +
                        " @out_venta_id=@id OUTPUT, @out_status_code=@code OUTPUT, @out_message=@message OUTPUT; " +
                        "SELECT @id AS id, @code AS code, @message AS message;";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, cotizacionId);
            ps.setInt(2, bodegaId);
            ps.setInt(3, serieId);
            if (cajeroId != null) ps.setInt(4, cajeroId); else ps.setNull(4, Types.INTEGER);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("sp_cotizaciones_to_venta sin resultado");
                int code = rs.getInt("code");
                // algunos scripts antiguos devuelven 'message' o 'msg'; tomamos 'message' y caemos a 'msg' si no está
                String message;
                try { message = rs.getString("message"); }
                catch (SQLException ex) { message = rs.getString("msg"); }
                int id = rs.getInt("id");
                if (code != 0) throw new SQLException("sp_cotizaciones_to_venta ("+code+"): " + message);
                return id;
            }
        }
    }
}
