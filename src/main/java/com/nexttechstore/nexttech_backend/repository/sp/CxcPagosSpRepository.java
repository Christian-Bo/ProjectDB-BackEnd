package com.nexttechstore.nexttech_backend.repository.sp;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Repo para SPs de pagos CxC:
 *  - sp_cxc_pagos_list
 *  - sp_cxc_pagos_aplicaciones
 */
@Repository
public class CxcPagosSpRepository {

    private final DataSource dataSource;

    public CxcPagosSpRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Listado de pagos (clienteId / desde / hasta) */
    public List<Map<String, Object>> listarPagos(Integer clienteId, java.sql.Date desde, java.sql.Date hasta) throws SQLException {
        final String sql = "EXEC dbo.sp_cxc_pagos_list @p_cliente_id=?, @p_desde=?, @p_hasta=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (clienteId != null) ps.setInt(1, clienteId); else ps.setNull(1, Types.INTEGER);
            if (desde != null)     ps.setDate(2, desde);    else ps.setNull(2, Types.DATE);
            if (hasta != null)     ps.setDate(3, hasta);    else ps.setNull(3, Types.DATE);

            List<Map<String,Object>> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> r = new HashMap<>();
                    r.put("pagoId",            rs.getInt("pago_id"));
                    r.put("fechaPago",         asLocalDate(rs.getDate("fecha_pago")));
                    r.put("clienteId",         getMaybeNullInt(rs, "cliente_id"));
                    r.put("clienteCodigo",     rs.getString("cliente_codigo"));
                    r.put("clienteNombre",     rs.getString("cliente_nombre"));
                    r.put("formaPago",         rs.getString("forma_pago"));
                    r.put("montoTotal",        rs.getBigDecimal("monto_total"));
                    r.put("observaciones",     rs.getString("observaciones"));
                    r.put("totalAplicado",     rs.getBigDecimal("total_aplicado"));
                    r.put("numAplicaciones",   rs.getInt("num_aplicaciones"));
                    list.add(r);
                }
            }
            return list;
        }
    }

    /** Detalle de aplicaciones por pago (SP ya oculta documentos anulados) */
    public List<Map<String, Object>> listarAplicacionesPorPago(Integer pagoId) throws SQLException {
        final String sql = "EXEC dbo.sp_cxc_pagos_aplicaciones @p_pago_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pagoId);

            List<Map<String,Object>> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> r = new HashMap<>();
                    // obligatorios del SP
                    r.put("aplicacionId",    safeInt(rs, "aplicacion_id"));
                    r.put("pagoId",          safeInt(rs, "pago_id"));
                    r.put("documentoId",     safeIntNullable(rs, "documento_id"));
                    r.put("numeroDocumento", safeStr(rs, "numero_documento"));
                    r.put("montoAplicado",   rs.getBigDecimal("monto_aplicado"));
                    r.put("fechaAplicacion", asLocalDate(rs.getDate("fecha_aplicacion")));

                    // opcionales (si el SP los expone se leerán, si no quedarán en null)
                    r.put("origenTipo",      safeStr(rs, "origen_tipo"));
                    r.put("origenId",        safeIntNullable(rs, "origen_id"));
                    r.put("formaPago",       safeStr(rs, "forma_pago"));
                    r.put("observaciones",   safeStr(rs, "observaciones"));

                    list.add(r);
                }
            }
            return list;
        }
    }

    private static Integer safeIntNullable(ResultSet rs, String col) throws SQLException {
        try {
            int v = rs.getInt(col);
            return rs.wasNull() ? null : v;
        } catch (SQLException ignore) { return null; }
    }
    private static Integer safeInt(ResultSet rs, String col) throws SQLException {
        try { return rs.getInt(col); } catch (SQLException e) { return null; }
    }
    private static String safeStr(ResultSet rs, String col) throws SQLException {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }

    // ===== helpers =====
    private static Integer getMaybeNullInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }
    private static java.time.LocalDate asLocalDate(java.sql.Date d) {
        return (d != null ? d.toLocalDate() : null);
    }
}
