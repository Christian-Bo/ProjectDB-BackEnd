package com.nexttechstore.nexttech_backend.repository.sp;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

@Repository
public class VentasPagosSpRepository {

    private final DataSource dataSource;

    public VentasPagosSpRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ===== Helpers tolerantes a alias/columnas =====
    private static Integer readIntAny(ResultSet rs, String... cols) throws SQLException {
        for (String c : cols) {
            try {
                int v = rs.getInt(c);
                if (!rs.wasNull()) return v;
            } catch (SQLException ignore) {}
            try {
                Object o = rs.getObject(c);
                if (o instanceof Number n) return n.intValue();
                if (o instanceof String s && !s.isBlank()) return Integer.parseInt(s.trim());
            } catch (Exception ignore) {}
        }
        return null;
    }
    private static String readStrAny(ResultSet rs, String... cols) throws SQLException {
        for (String c : cols) {
            try {
                String s = rs.getString(c);
                if (s != null) return s;
            } catch (SQLException ignore) {}
        }
        return null;
    }
    private static java.sql.Date readSqlDateAny(ResultSet rs, String... cols) throws SQLException {
        for (String c : cols) {
            try {
                java.sql.Date d = rs.getDate(c);
                if (d != null) return d;
            } catch (SQLException ignore) {}
            try {
                java.sql.Timestamp ts = rs.getTimestamp(c);
                if (ts != null) return new java.sql.Date(ts.getTime());
            } catch (SQLException ignore) {}
        }
        return null;
    }

    /** Listado de pagos por filtros (ventaId, clienteId, desde, hasta) */
    public List<Map<String,Object>> listarPagos(Integer ventaId, Integer clienteId,
                                                java.sql.Date desde, java.sql.Date hasta) throws SQLException {
        final String sql = "EXEC dbo.sp_ventas_pagos_list @p_venta_id=?, @p_cliente_id=?, @p_desde=?, @p_hasta=?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (ventaId != null)   ps.setInt(1, ventaId);   else ps.setNull(1, Types.INTEGER);
            if (clienteId != null) ps.setInt(2, clienteId); else ps.setNull(2, Types.INTEGER);
            if (desde != null)     ps.setDate(3, desde);    else ps.setNull(3, Types.DATE);
            if (hasta != null)     ps.setDate(4, hasta);    else ps.setNull(4, Types.DATE);

            List<Map<String,Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Map<String,Object> m = new HashMap<>();

                    Integer pagoId   = readIntAny(rs, "pago_id", "id", "venta_pago_id");
                    Integer vtaId    = readIntAny(rs, "venta_id", "id_venta");
                    String  numVenta = readStrAny(rs, "numero_venta", "num_venta", "venta_numero");
                    java.sql.Date fv = readSqlDateAny(rs, "fecha_venta", "f_venta", "fecha");

                    Integer cliId    = readIntAny(rs, "cliente_id", "id_cliente");
                    String  cliCod   = readStrAny(rs, "cliente_codigo", "codigo_cliente");
                    String  cliNom   = readStrAny(rs, "cliente_nombre", "nombre_cliente");

                    String forma     = readStrAny(rs, "forma_pago", "forma", "fpago");
                    java.math.BigDecimal monto = null;
                    try { monto = rs.getBigDecimal("monto"); } catch (SQLException ignore) {}
                    if (monto == null) {
                        try { monto = rs.getBigDecimal("total"); } catch (SQLException ignore) {}
                    }
                    String ref = readStrAny(rs, "referencia", "obs", "observaciones");

                    m.put("pagoId",        pagoId);
                    m.put("ventaId",       vtaId);
                    m.put("numeroVenta",   numVenta);
                    m.put("fechaVenta",    fv != null ? fv.toLocalDate() : null);
                    m.put("clienteId",     cliId);
                    m.put("clienteCodigo", cliCod);
                    m.put("clienteNombre", cliNom);
                    m.put("formaPago",     forma);
                    m.put("monto",         monto);
                    m.put("referencia",    ref);
                    out.add(m);
                }
            } catch (SQLException e) {
                // Log de columnas reales devueltas (debug rápido)
                try {
                    ResultSetMetaData md = ps.getMetaData();
                    if (md != null) {
                        int n = md.getColumnCount();
                        List<String> cols = new ArrayList<>();
                        for (int i=1;i<=n;i++) cols.add(md.getColumnLabel(i));
                        System.err.println("[sp_ventas_pagos_list] columnas detectadas: " + cols);
                    }
                } catch (Exception ignore) {}
                throw e;
            }
            return out;
        }
    }

    /** Crear pago a una venta */
    public int crearPago(int ventaId, String formaPago, java.math.BigDecimal monto, String referencia) throws SQLException {
        final String sql =
                "DECLARE @id INT, @code INT, @msg NVARCHAR(200); " +
                        "EXEC dbo.sp_ventas_pagos_add @p_venta_id=?, @p_forma_pago=?, @p_monto=?, @p_referencia=?, " +
                        "  @out_pago_id=@id OUTPUT, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT; " +
                        "SELECT @id AS id, @code AS code, @msg AS msg;";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, ventaId);
            if (formaPago == null || formaPago.isBlank()) ps.setNull(2, Types.NVARCHAR); else ps.setString(2, formaPago);
            ps.setBigDecimal(3, monto);
            if (referencia == null) ps.setNull(4, Types.NVARCHAR); else ps.setString(4, referencia);

            try (ResultSet rs = ps.executeQuery()){
                if (!rs.next()) throw new SQLException("sp_ventas_pagos_add sin resultado");
                int code = rs.getInt("code");
                String msg = rs.getString("msg");
                int id = rs.getInt("id");
                if (code != 0) throw new SQLException("sp_ventas_pagos_add ("+code+"): "+msg);
                return id;
            }
        }
    }

    /** Eliminar (anular) un pago */
    public void eliminarPago(int pagoId) throws SQLException {
        final String sql =
                "DECLARE @code INT, @msg NVARCHAR(200); " +
                        "EXEC dbo.sp_ventas_pagos_delete @p_pago_id=?, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT; " +
                        "SELECT @code AS code, @msg AS msg;";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, pagoId);
            try (ResultSet rs = ps.executeQuery()){
                if (!rs.next()) throw new SQLException("sp_ventas_pagos_delete sin resultado");
                int code = rs.getInt("code");
                String msg = rs.getString("msg");
                if (code != 0) throw new SQLException("sp_ventas_pagos_delete ("+code+"): "+msg);
            }
        }
    }
}
