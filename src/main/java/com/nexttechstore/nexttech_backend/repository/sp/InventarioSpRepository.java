package com.nexttechstore.nexttech_backend.repository.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InventarioSpRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /* ===================== INVENTARIO (STOCK) ===================== */

    public List<Map<String, Object>> listarInventario(Integer bodegaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_inventario_listar")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("bodega_id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("bodega_id", bodegaId);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public List<Map<String, Object>> inventarioPorProducto(Integer productoId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_inventario_por_producto")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("producto_id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("producto_id", productoId);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public List<Map<String, Object>> inventarioPorBodega(Integer bodegaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_inventario_por_bodega")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("bodega_id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("bodega_id", bodegaId);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    /* ===================== MOVIMIENTOS (KARDEX) ===================== */
    // Llama al SP real que mostraste: dbo.sp_movimientos_listar
    // @desde DATETIME2(3)  | @hasta DATETIME2(3) (exclusivo en la consulta)
    // @producto_id INT     | @bodega_id INT     | @tipo CHAR(1)
    public List<Map<String, Object>> listarMovimientos(String fechaDesde, String fechaHasta,
                                                       Integer productoId, Integer bodegaId, String tipo) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_movimientos_listar")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("desde", Types.TIMESTAMP),
                        new SqlParameter("hasta", Types.TIMESTAMP),
                        new SqlParameter("producto_id", Types.INTEGER),
                        new SqlParameter("bodega_id", Types.INTEGER),
                        new SqlParameter("tipo", Types.CHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("desde", toTsStart(fechaDesde));      // "YYYY-MM-DD" -> 00:00:00.000
        params.put("hasta", toTsExclusive(fechaHasta));  // "YYYY-MM-DD" -> (día+1) 00:00:00.000 (porque el SP usa "< @hasta")
        params.put("producto_id", productoId);
        params.put("bodega_id", bodegaId);
        params.put("tipo", (tipo == null || tipo.trim().isEmpty()) ? null : tipo.trim());

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    /* ===================== ALERTAS ===================== */

    public List<Map<String, Object>> listarAlertas(Integer bodegaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_alertas_stock_listar")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("bodega_id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("bodega_id", bodegaId);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    /* ===================== Helpers fechas ===================== */

    // "2025-11-01" -> 2025-11-01 00:00:00.000
    private static Timestamp toTsStart(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            if (s.length() >= 19) { // ISO con hora
                return Timestamp.valueOf(s.substring(0, 19).replace('T', ' '));
            }
            LocalDate d = LocalDate.parse(s.substring(0, 10));
            return Timestamp.valueOf(d.atStartOfDay());
        } catch (Exception e) {
            return null;
        }
    }

    // "2025-11-01" -> 2025-11-02 00:00:00.000 (exclusivo)
    private static Timestamp toTsExclusive(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            if (s.length() >= 19) {
                // Si ya viene con hora, avanzamos 1 ms para simular exclusividad mínima
                LocalDateTime dt = LocalDateTime.parse(s.substring(0, 19).replace('T', ' ').
                        replace(' ', 'T'));
                return Timestamp.valueOf(dt.plusNanos(1_000_000)); // +1 ms
            }
            LocalDate d = LocalDate.parse(s.substring(0, 10));
            return Timestamp.valueOf(d.plusDays(1).atStartOfDay());
        } catch (Exception e) {
            return null;
        }
    }
}
