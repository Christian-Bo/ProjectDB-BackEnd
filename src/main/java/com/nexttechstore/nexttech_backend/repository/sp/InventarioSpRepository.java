package com.nexttechstore.nexttech_backend.repository.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InventarioSpRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> listarInventario(Integer bodegaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_inventario_listar")
                .declareParameters(new SqlParameter("bodega_id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("bodega_id", bodegaId);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public List<Map<String, Object>> inventarioPorProducto(Integer productoId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_inventario_por_producto")
                .declareParameters(new SqlParameter("producto_id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("producto_id", productoId);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public List<Map<String, Object>> inventarioPorBodega(Integer bodegaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_inventario_por_bodega")
                .declareParameters(new SqlParameter("bodega_id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("bodega_id", bodegaId);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public List<Map<String, Object>> listarKardex(Integer productoId, Integer bodegaId,
                                                  String fechaDesde, String fechaHasta) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_kardex_listar")
                .declareParameters(
                        new SqlParameter("producto_id", Types.INTEGER),
                        new SqlParameter("bodega_id", Types.INTEGER),
                        new SqlParameter("fecha_desde", Types.DATE),
                        new SqlParameter("fecha_hasta", Types.DATE)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("producto_id", productoId);
        params.put("bodega_id", bodegaId);
        params.put("fecha_desde", fechaDesde);
        params.put("fecha_hasta", fechaHasta);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public List<Map<String, Object>> listarAlertas(Integer bodegaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_alertas_stock_listar")
                .declareParameters(new SqlParameter("bodega_id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("bodega_id", bodegaId);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }
}