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
public class AjusteInventarioSpRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> listarAjustes(Integer bodegaId, String tipoAjuste,
                                                   String fechaDesde, String fechaHasta) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_ajustes_inventario_listar")
                .declareParameters(
                        new SqlParameter("bodega_id", Types.INTEGER),
                        new SqlParameter("tipo_ajuste", Types.CHAR),
                        new SqlParameter("fecha_desde", Types.DATE),
                        new SqlParameter("fecha_hasta", Types.DATE)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("bodega_id", bodegaId);
        params.put("tipo_ajuste", tipoAjuste);
        params.put("fecha_desde", fechaDesde);
        params.put("fecha_hasta", fechaHasta);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public Map<String, Object> obtenerAjustePorId(Integer id) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_ajuste_obtener_por_id")
                .declareParameters(new SqlParameter("id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> lista = (List<Map<String, Object>>) result.get("#result-set-1");

        return lista.isEmpty() ? null : lista.get(0);
    }

    public List<Map<String, Object>> obtenerDetalleAjuste(Integer ajusteId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_ajuste_detalle_listar")
                .declareParameters(new SqlParameter("ajuste_id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("ajuste_id", ajusteId);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public Map<String, Object> crearAjuste(String numeroAjuste, String fechaAjuste,
                                           Integer bodegaId, String tipoAjuste, String motivo,
                                           Integer responsableId, String observaciones, String detallesJson) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_ajuste_crear")
                .declareParameters(
                        new SqlParameter("numero_ajuste", Types.NVARCHAR),
                        new SqlParameter("fecha_ajuste", Types.DATE),
                        new SqlParameter("bodega_id", Types.INTEGER),
                        new SqlParameter("tipo_ajuste", Types.CHAR),
                        new SqlParameter("motivo", Types.NVARCHAR),
                        new SqlParameter("responsable_id", Types.INTEGER),
                        new SqlParameter("observaciones", Types.NVARCHAR),
                        new SqlParameter("detalles", Types.NVARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("numero_ajuste", numeroAjuste);
        params.put("fecha_ajuste", fechaAjuste);
        params.put("bodega_id", bodegaId);
        params.put("tipo_ajuste", tipoAjuste);
        params.put("motivo", motivo);
        params.put("responsable_id", responsableId);
        params.put("observaciones", observaciones);
        params.put("detalles", detallesJson);

        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> lista = (List<Map<String, Object>>) result.get("#result-set-1");

        return lista.isEmpty() ? null : lista.get(0);
    }
}