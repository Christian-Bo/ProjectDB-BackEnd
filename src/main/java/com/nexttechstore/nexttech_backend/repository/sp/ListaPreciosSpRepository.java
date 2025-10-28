package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ListaPreciosSpRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApiResponse<ListaPreciosResponseDTO> create(ListaPreciosRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosCreate")
                    .declareParameters(
                            new SqlParameter("nombre", Types.NVARCHAR),
                            new SqlParameter("moneda", Types.NVARCHAR),
                            new SqlParameter("activa", Types.BIT),
                            new SqlOutParameter("listaId", Types.INTEGER),
                            new SqlOutParameter("listaNombre", Types.NVARCHAR)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("nombre", request.getNombre())
                    .addValue("moneda", request.getMoneda())
                    .addValue("activa", request.getActiva());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);

                ListaPreciosResponseDTO lista = ListaPreciosResponseDTO.builder()
                        .id((Integer) row.get("id"))
                        .nombre((String) row.get("nombre"))
                        .moneda((String) row.get("moneda"))
                        .build();

                return ApiResponse.success((String) row.get("mensaje"), lista);
            }

            return ApiResponse.error("No se pudo crear la lista de precios");

        } catch (Exception e) {
            log.error("Error al crear lista de precios: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Void> update(Integer id, ListaPreciosRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosUpdate")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER),
                            new SqlParameter("nombre", Types.NVARCHAR),
                            new SqlParameter("moneda", Types.NVARCHAR),
                            new SqlParameter("activa", Types.BIT)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("nombre", request.getNombre())
                    .addValue("moneda", request.getMoneda())
                    .addValue("activa", request.getActiva());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo actualizar la lista");

        } catch (Exception e) {
            log.error("Error al actualizar lista: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Void> delete(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosDelete")
                    .declareParameters(new SqlParameter("id", Types.INTEGER));

            MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo eliminar la lista");

        } catch (Exception e) {
            log.error("Error al eliminar lista: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Void> activate(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosActivar")
                    .declareParameters(new SqlParameter("id", Types.INTEGER));

            MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo activar la lista");

        } catch (Exception e) {
            log.error("Error al activar lista: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public Optional<ListaPreciosResponseDTO> getById(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosGetById")
                    .declareParameters(new SqlParameter("id", Types.INTEGER))
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToLista(rs));

            MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<ListaPreciosResponseDTO> listas = (List<ListaPreciosResponseDTO>) result.get("#result-set-1");

            return listas != null && !listas.isEmpty() ? Optional.of(listas.get(0)) : Optional.empty();

        } catch (Exception e) {
            log.error("Error al obtener lista por ID: ", e);
            return Optional.empty();
        }
    }

    public ApiResponse<List<ListaPreciosResponseDTO>> getAll(Boolean soloActivas) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosGetAll")
                    .declareParameters(new SqlParameter("soloActivas", Types.BIT))
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToLista(rs));

            MapSqlParameterSource params = new MapSqlParameterSource().addValue("soloActivas", soloActivas);
            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<ListaPreciosResponseDTO> listas = (List<ListaPreciosResponseDTO>) result.get("#result-set-1");

            if (listas != null) {
                return ApiResponse.success("Listas obtenidas", listas, listas.size());
            }

            return ApiResponse.error("No se pudieron obtener las listas");

        } catch (Exception e) {
            log.error("Error al obtener listas: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Map<String, Object>> copiar(ListaPreciosCopiarRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosCopiar")
                    .declareParameters(
                            new SqlParameter("lista_origen_id", Types.INTEGER),
                            new SqlParameter("nombre_nueva_lista", Types.NVARCHAR),
                            new SqlParameter("porcentaje_ajuste", Types.DECIMAL),
                            new SqlParameter("moneda", Types.NVARCHAR),
                            new SqlParameter("activa", Types.BIT),
                            new SqlOutParameter("nueva_lista_id", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("lista_origen_id", request.getListaOrigenId())
                    .addValue("nombre_nueva_lista", request.getNombreNuevaLista())
                    .addValue("porcentaje_ajuste", request.getPorcentajeAjuste())
                    .addValue("moneda", request.getMoneda())
                    .addValue("activa", request.getActiva());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                return ApiResponse.success("Lista copiada exitosamente", resultList.get(0));
            }

            return ApiResponse.error("No se pudo copiar la lista");

        } catch (Exception e) {
            log.error("Error al copiar lista: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    private ListaPreciosResponseDTO mapRowToLista(ResultSet rs) throws SQLException {
        return ListaPreciosResponseDTO.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .moneda(rs.getString("moneda"))
                .activa(rs.getBoolean("activa"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .totalProductos(rs.getInt("total_productos"))
                .totalClientes(rs.getInt("total_clientes"))
                .build();
    }
}