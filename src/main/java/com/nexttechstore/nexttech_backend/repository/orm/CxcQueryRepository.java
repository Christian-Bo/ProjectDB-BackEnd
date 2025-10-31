package com.nexttechstore.nexttech_backend.repository.orm;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
public class CxcQueryRepository {

    private final DataSource dataSource;
    public CxcQueryRepository(DataSource ds){ this.dataSource = ds; }

    public List<Map<String,Object>> listarDocumentos(Integer clienteId, String estado, Date desde, Date hasta){
        StringBuilder sql = new StringBuilder(
                "SELECT id AS documento_id, cliente_id, origen_tipo, origen_id, numero_documento, " +
                        "       fecha_emision, fecha_vencimiento, moneda, monto_total, saldo_pendiente, estado " +
                        "FROM dbo.cxc_documentos WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (clienteId != null){ sql.append(" AND cliente_id = ?"); params.add(clienteId); }
        if (estado != null && !estado.isBlank()){ sql.append(" AND estado = ?"); params.add(estado); }
        if (desde != null){ sql.append(" AND fecha_emision >= ?"); params.add(desde); }
        if (hasta != null){ sql.append(" AND fecha_emision <= ?"); params.add(hasta); }

        sql.append(" ORDER BY fecha_emision, id");

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++){
                Object p = params.get(i);
                if (p instanceof java.util.Date)
                    ps.setDate(i+1, new java.sql.Date(((java.util.Date)p).getTime()));
                else if (p instanceof Integer)
                    ps.setInt(i+1, (Integer) p);
                else
                    ps.setString(i+1, String.valueOf(p));
            }

            List<Map<String,Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("documentoId",      rs.getInt("documento_id"));
                    m.put("clienteId",        rs.getInt("cliente_id"));
                    m.put("origenTipo",       rs.getString("origen_tipo"));
                    m.put("origenId",         rs.getInt("origen_id"));
                    m.put("numeroDocumento",  rs.getString("numero_documento"));
                    m.put("fechaEmision",     rs.getDate("fecha_emision"));
                    m.put("fechaVencimiento", rs.getDate("fecha_vencimiento"));
                    m.put("moneda",           rs.getString("moneda"));
                    m.put("montoTotal",       rs.getBigDecimal("monto_total"));
                    m.put("saldoPendiente",   rs.getBigDecimal("saldo_pendiente"));
                    m.put("estado",           rs.getString("estado"));
                    out.add(m);
                }
            }
            return out;
        } catch (SQLException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    public Map<String,Object> getDocumentoById(int documentoId){
        String sql = "SELECT id AS documento_id, cliente_id, origen_tipo, origen_id, numero_documento, " +
                "fecha_emision, fecha_vencimiento, moneda, monto_total, saldo_pendiente, estado " +
                "FROM dbo.cxc_documentos WHERE id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, documentoId);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("documentoId",      rs.getInt("documento_id"));
                    m.put("clienteId",        rs.getInt("cliente_id"));
                    m.put("origenTipo",       rs.getString("origen_tipo"));
                    m.put("origenId",         rs.getInt("origen_id"));
                    m.put("numeroDocumento",  rs.getString("numero_documento"));
                    m.put("fechaEmision",     rs.getDate("fecha_emision"));
                    m.put("fechaVencimiento", rs.getDate("fecha_vencimiento"));
                    m.put("moneda",           rs.getString("moneda"));
                    m.put("montoTotal",       rs.getBigDecimal("monto_total"));
                    m.put("saldoPendiente",   rs.getBigDecimal("saldo_pendiente"));
                    m.put("estado",           rs.getString("estado"));
                    return m;
                }
            }
            throw new SQLException("cxc_documento no encontrado id=" + documentoId);
        } catch (SQLException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** Historial de aplicaciones (abonos) de un documento, con datos del pago */
    public List<Map<String,Object>> listarAplicacionesPorDocumento(int documentoId){
        String sql =
                "SELECT a.id AS aplicacion_id, a.pago_id, a.monto_aplicado, a.fecha_aplicacion, " +
                        "       p.fecha_pago, p.forma_pago, p.observaciones " +
                        "FROM dbo.cxc_aplicaciones a " +
                        "JOIN dbo.cxc_pagos p ON p.id = a.pago_id " +
                        "WHERE a.documento_id = ? " +
                        "ORDER BY a.fecha_aplicacion, a.id";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, documentoId);
            List<Map<String,Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("aplicacionId",    rs.getInt("aplicacion_id"));
                    m.put("pagoId",          rs.getInt("pago_id"));
                    m.put("montoAplicado",   rs.getBigDecimal("monto_aplicado"));
                    m.put("fechaAplicacion", rs.getDate("fecha_aplicacion"));
                    m.put("fechaPago",       rs.getDate("fecha_pago"));
                    m.put("formaPago",       rs.getString("forma_pago"));
                    m.put("observaciones",   rs.getString("observaciones"));
                    out.add(m);
                }
            }
            return out;
        } catch (SQLException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** (Opcional) Listado de pagos por filtros simples */
    public List<Map<String,Object>> listarPagos(Integer clienteId, Date desde, Date hasta){
        StringBuilder sql = new StringBuilder(
                "SELECT id AS pago_id, cliente_id, fecha_pago, forma_pago, monto_total, observaciones, fecha_creacion " +
                        "FROM dbo.cxc_pagos WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (clienteId != null){ sql.append(" AND cliente_id=?"); params.add(clienteId); }
        if (desde != null){ sql.append(" AND fecha_pago >= ?"); params.add(desde); }
        if (hasta != null){ sql.append(" AND fecha_pago <= ?"); params.add(hasta); }
        sql.append(" ORDER BY fecha_pago DESC, id DESC");

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i=0;i<params.size();i++){
                Object p = params.get(i);
                if (p instanceof java.util.Date) ps.setDate(i+1, new java.sql.Date(((java.util.Date)p).getTime()));
                else if (p instanceof Integer)   ps.setInt(i+1, (Integer)p);
                else                              ps.setString(i+1, String.valueOf(p));
            }
            List<Map<String,Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("pagoId",        rs.getInt("pago_id"));
                    m.put("clienteId",     rs.getInt("cliente_id"));
                    m.put("fechaPago",     rs.getDate("fecha_pago"));
                    m.put("formaPago",     rs.getString("forma_pago"));
                    m.put("montoTotal",    rs.getBigDecimal("monto_total"));
                    m.put("observaciones", rs.getString("observaciones"));
                    m.put("fechaCreacion", rs.getTimestamp("fecha_creacion"));
                    out.add(m);
                }
            }
            return out;
        } catch (SQLException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
