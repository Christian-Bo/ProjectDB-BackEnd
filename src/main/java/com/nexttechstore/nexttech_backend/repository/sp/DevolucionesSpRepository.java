package com.nexttechstore.nexttech_backend.repository.sp;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.nexttechstore.nexttech_backend.dto.DevolucionCreateRequest;
import com.nexttechstore.nexttech_backend.dto.DevolucionItemDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Repository
public class DevolucionesSpRepository {

    private final DataSource dataSource;

    public DevolucionesSpRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** EXEC dbo.sp_devoluciones_venta_add (...); retorna id de la devolución creada */
    public int crearDevolucion(DevolucionCreateRequest req) throws SQLException {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new SQLException("La devolución debe tener al menos 1 ítem");
        }

        // TVP EXACTO: (detalle_venta_id INT, producto_id INT, cantidad INT, observaciones NVARCHAR(MAX) NULL)
        SQLServerDataTable tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("detalle_venta_id", Types.INTEGER);
        tvp.addColumnMetadata("producto_id", Types.INTEGER);
        tvp.addColumnMetadata("cantidad", Types.INTEGER);
        tvp.addColumnMetadata("observaciones", Types.NVARCHAR);

        for (DevolucionItemDto it : req.getItems()) {
            tvp.addRow(
                    it.getDetalleVentaId(),
                    it.getProductoId(),
                    it.getCantidad(),
                    it.getObservaciones()
            );
        }

        String sql =
                "DECLARE @out_id INT, @code INT, @msg NVARCHAR(400);\n" +
                        "EXEC dbo.sp_devoluciones_venta_add " +
                        "  @p_venta_id=?, @p_aprobada_por=?, @p_detalle=?, " +
                        "  @out_devol_id=@out_id OUTPUT, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT;\n" +
                        "SELECT @out_id AS id, @code AS code, @msg AS msg;";

        try (Connection conn = dataSource.getConnection()) {
            SQLServerConnection sqlConn = conn.unwrap(SQLServerConnection.class);
            try (SQLServerPreparedStatement ps = (SQLServerPreparedStatement) sqlConn.prepareStatement(sql)) {
                ps.setInt(1, req.getVentaId());
                ps.setInt(2, req.getAprobadaPor());
                ps.setStructured(3, "dbo.tvp_devolucion_detalle", tvp);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Sin resultado de sp_devoluciones_venta_add");
                    int id = rs.getInt("id");
                    int code = rs.getInt("code");
                    String msg = rs.getString("msg");
                    if (code != 0) throw new SQLException("SP sp_devoluciones_venta_add (" + code + "): " + msg);
                    if (id <= 0) throw new SQLException("Devolución sin id válido");
                    return id;
                }
            }
        }
    }

    /**
     * EXEC dbo.sp_devoluciones_venta_anular @p_devolucion_id=?, @p_usuario_id=?, @out_status_code=?, @out_message=?;
     */
    public void anularDevolucion(int devolucionId, int usuarioId) throws SQLException {
        String sql =
                "DECLARE @code INT, @msg NVARCHAR(400);\n" +
                        "EXEC dbo.sp_devoluciones_venta_anular " +
                        "  @p_devolucion_id=?, @p_usuario_id=?, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT;\n" +
                        "SELECT @code AS code, @msg AS msg;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, devolucionId);
            ps.setInt(2, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Sin resultado de sp_devoluciones_venta_anular");
                int code = rs.getInt("code");
                String msg = rs.getString("msg");
                if (code != 0) throw new SQLException("SP sp_devoluciones_venta_anular (" + code + "): " + msg);
            }
        }
    }

    /** EXEC dbo.sp_devoluciones_get_by_id @p_devolucion_id=?  -> 2 resultsets (header, detalle) */
    public Map<String, Object> getDevolucionById(int devolucionId) throws SQLException {
        String sql = "EXEC dbo.sp_devoluciones_get_by_id @p_devolucion_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, devolucionId);

            Map<String, Object> header = null;
            List<Map<String, Object>> detalle = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                // RS1: header
                if (rs.next()) {
                    header = new HashMap<>();
                    header.put("id", rs.getInt("id"));
                    header.put("numero_devolucion", safeGetString(rs, "numero_devolucion"));
                    header.put("fecha_devolucion", rs.getDate("fecha_devolucion"));
                    header.put("venta_id", rs.getInt("venta_id"));
                    header.put("aprobada_por", rs.getInt("aprobada_por"));
                    // columnas opcionales
                    safePutBigDecimal(rs, header, "total_devolucion");
                    safePutString(rs, header, "estado");
                }
            }

            // RS2: detalle
            if (((Statement) ps).getMoreResults()) {
                try (ResultSet rs2 = ((Statement) ps).getResultSet()) {
                    while (rs2.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("detalle_id", rs2.getInt("detalle_id"));
                        row.put("detalle_venta_id", rs2.getInt("detalle_venta_id"));
                        row.put("producto_id", rs2.getInt("producto_id"));
                        row.put("cantidad", rs2.getInt("cantidad"));
                        detalle.add(row);
                    }
                }
            }

            if (header == null) {
                throw new SQLException("Devolución no encontrada id=" + devolucionId);
            }
            Map<String, Object> out = new HashMap<>();
            out.put("header", header);
            out.put("detalle", detalle);
            return out;
        }
    }

    /**
     * EXEC dbo.sp_devoluciones_list @p_desde=?, @p_hasta=?, @p_venta_id=?, @p_cliente_id=?, @p_page=?, @p_size=?
     * Devuelve lista de devoluciones (columnas mínimas y opcionales).
     */
    public List<Map<String, Object>> listarDevoluciones(
            LocalDate desde,
            LocalDate hasta,
            Integer ventaId,
            Integer clienteId,
            Integer page,
            Integer size
    ) throws SQLException {
        String sql = "EXEC dbo.sp_devoluciones_list @p_desde=?, @p_hasta=?, @p_venta_id=?, @p_cliente_id=?, @p_page=?, @p_size=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (desde != null) ps.setDate(1, Date.valueOf(desde)); else ps.setNull(1, Types.DATE);
            if (hasta != null) ps.setDate(2, Date.valueOf(hasta)); else ps.setNull(2, Types.DATE);
            if (ventaId != null) ps.setInt(3, ventaId); else ps.setNull(3, Types.INTEGER);
            if (clienteId != null) ps.setInt(4, clienteId); else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, page != null ? page : 0);
            ps.setInt(6, size != null ? size : 50);

            List<Map<String, Object>> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("numero_devolucion", safeGetString(rs, "numero_devolucion"));
                    row.put("fecha_devolucion", rs.getDate("fecha_devolucion"));
                    row.put("venta_id", rs.getInt("venta_id"));
                    // opcionales
                    safePutInteger(rs, row, "cliente_id");
                    safePutString(rs, row, "cliente_nombre");
                    safePutBigDecimal(rs, row, "total_devolucion");
                    safePutString(rs, row, "estado");
                    list.add(row);
                }
            }
            return list;
        }
    }

    // --- helpers tolerantes a columnas opcionales ---
    private static String safeGetString(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }
    private static void safePutString(ResultSet rs, Map<String,Object> m, String col) {
        try { m.put(col, rs.getString(col)); } catch (SQLException ignored) {}
    }
    private static void safePutBigDecimal(ResultSet rs, Map<String,Object> m, String col) {
        try { m.put(col, rs.getBigDecimal(col)); } catch (SQLException ignored) {}
    }
    private static void safePutInteger(ResultSet rs, Map<String,Object> m, String col) {
        try { m.put(col, (Integer) rs.getObject(col)); } catch (SQLException ignored) {}
    }
}
