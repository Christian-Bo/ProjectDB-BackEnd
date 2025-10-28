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
public class BodegaSpRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> listarBodegas() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_bodegas_listar");

        Map<String, Object> result = jdbcCall.execute();
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public Map<String, Object> obtenerBodegaPorId(Integer id) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_bodega_obtener_por_id")
                .declareParameters(new SqlParameter("id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> lista = (List<Map<String, Object>>) result.get("#result-set-1");

        return lista.isEmpty() ? null : lista.get(0);
    }

    public Map<String, Object> crearBodega(String codigo, String nombre, String ubicacion,
                                           Integer capacidadMaxima, Integer responsableId,
                                           String telefono, String email) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_bodega_crear")
                .declareParameters(
                        new SqlParameter("codigo", Types.NVARCHAR),
                        new SqlParameter("nombre", Types.NVARCHAR),
                        new SqlParameter("ubicacion", Types.NVARCHAR),
                        new SqlParameter("capacidad_maxima", Types.INTEGER),
                        new SqlParameter("responsable_id", Types.INTEGER),
                        new SqlParameter("telefono", Types.NVARCHAR),
                        new SqlParameter("email", Types.NVARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("codigo", codigo);
        params.put("nombre", nombre);
        params.put("ubicacion", ubicacion);
        params.put("capacidad_maxima", capacidadMaxima);
        params.put("responsable_id", responsableId);
        params.put("telefono", telefono);
        params.put("email", email);

        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> lista = (List<Map<String, Object>>) result.get("#result-set-1");

        return lista.isEmpty() ? null : lista.get(0);
    }

    public void actualizarBodega(Integer id, String codigo, String nombre, String ubicacion,
                                 Integer capacidadMaxima, Integer responsableId,
                                 String telefono, String email) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_bodega_actualizar")
                .declareParameters(
                        new SqlParameter("id", Types.INTEGER),
                        new SqlParameter("codigo", Types.NVARCHAR),
                        new SqlParameter("nombre", Types.NVARCHAR),
                        new SqlParameter("ubicacion", Types.NVARCHAR),
                        new SqlParameter("capacidad_maxima", Types.INTEGER),
                        new SqlParameter("responsable_id", Types.INTEGER),
                        new SqlParameter("telefono", Types.NVARCHAR),
                        new SqlParameter("email", Types.NVARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("codigo", codigo);
        params.put("nombre", nombre);
        params.put("ubicacion", ubicacion);
        params.put("capacidad_maxima", capacidadMaxima);
        params.put("responsable_id", responsableId);
        params.put("telefono", telefono);
        params.put("email", email);

        jdbcCall.execute(params);
    }

    public void eliminarBodega(Integer id) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_bodega_eliminar")
                .declareParameters(new SqlParameter("id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        jdbcCall.execute(params);
    }
}