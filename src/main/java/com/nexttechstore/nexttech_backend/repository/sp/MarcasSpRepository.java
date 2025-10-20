
package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.catalogos.MarcaRequestDTO;
import com.nexttechstore.nexttech_backend.dto.catalogos.MarcaResponseDTO;
import com.nexttechstore.nexttech_backend.dto.common.ApiResponse;
import com.nexttechstore.nexttech_backend.dto.common.PageRequest;
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
public class MarcasSpRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Crear una nueva marca
     */
    public ApiResponse<MarcaResponseDTO> create(MarcaRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_MarcaCreate")
                    .declareParameters(
                            new SqlParameter("nombre", Types.NVARCHAR),
                            new SqlParameter("descripcion", Types.NVARCHAR),
                            new SqlParameter("activo", Types.BIT),
                            new SqlOutParameter("marcaId", Types.INTEGER),
                            new SqlOutParameter("marcaNombre", Types.NVARCHAR)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("nombre", request.getNombre())
                    .addValue("descripcion", request.getDescripcion())
                    .addValue("activo", request.getActivo());

            Map<String, Object> result = jdbcCall.execute(params);

            // Obtener el ResultSet del SP
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);

                MarcaResponseDTO marca = MarcaResponseDTO.builder()
                        .id((Integer) row.get("id"))
                        .nombre((String) row.get("nombre"))
                        .build();

                return ApiResponse.success(
                        (String) row.get("mensaje"),
                        marca
                );
            }

            return ApiResponse.error("No se pudo crear la marca");

        } catch (Exception e) {
            log.error("Error al crear marca: ", e);
            return ApiResponse.error("Error al crear marca: " + e.getMessage());
        }
    }

    /**
     * Actualizar una marca existente
     */
    public ApiResponse<Void> update(Integer id, MarcaRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_MarcaUpdate")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER),
                            new SqlParameter("nombre", Types.NVARCHAR),
                            new SqlParameter("descripcion", Types.NVARCHAR),
                            new SqlParameter("activo", Types.BIT)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("nombre", request.getNombre())
                    .addValue("descripcion", request.getDescripcion())
                    .addValue("activo", request.getActivo());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo actualizar la marca");

        } catch (Exception e) {
            log.error("Error al actualizar marca: ", e);
            return ApiResponse.error("Error al actualizar marca: " + e.getMessage());
        }
    }

    /**
     * Eliminar (desactivar) una marca
     */
    public ApiResponse<Void> delete(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_MarcaDelete")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo eliminar la marca");

        } catch (Exception e) {
            log.error("Error al eliminar marca: ", e);
            return ApiResponse.error("Error al eliminar marca: " + e.getMessage());
        }
    }

    /**
     * Activar una marca
     */
    public ApiResponse<Void> activate(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_MarcaActivar")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo activar la marca");

        } catch (Exception e) {
            log.error("Error al activar marca: ", e);
            return ApiResponse.error("Error al activar marca: " + e.getMessage());
        }
    }

    /**
     * Obtener una marca por ID
     */
    public Optional<MarcaResponseDTO> getById(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_MarcaGetById")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToMarcaResponse(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<MarcaResponseDTO> marcas = (List<MarcaResponseDTO>) result.get("#result-set-1");

            return marcas != null && !marcas.isEmpty()
                    ? Optional.of(marcas.get(0))
                    : Optional.empty();

        } catch (Exception e) {
            log.error("Error al obtener marca por ID: ", e);
            return Optional.empty();
        }
    }

    /**
     * Obtener todas las marcas con paginación
     */
    public ApiResponse<List<MarcaResponseDTO>> getAll(Boolean soloActivas, PageRequest pageRequest) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_MarcaGetAll")
                    .declareParameters(
                            new SqlParameter("soloActivas", Types.BIT)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToMarcaResponse(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("soloActivas", soloActivas);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<MarcaResponseDTO> marcas = (List<MarcaResponseDTO>) result.get("#result-set-1");

            if (marcas != null) {
                return ApiResponse.success(
                        "Marcas obtenidas exitosamente",
                        marcas,
                        marcas.size()
                );
            }

            return ApiResponse.error("No se pudieron obtener las marcas");

        } catch (Exception e) {
            log.error("Error al obtener marcas: ", e);
            return ApiResponse.error("Error al obtener marcas: " + e.getMessage());
        }
    }

    /**
     * Mapear ResultSet a MarcaResponseDTO
     */
    private MarcaResponseDTO mapRowToMarcaResponse(ResultSet rs) throws SQLException {
        return MarcaResponseDTO.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .activo(rs.getBoolean("activo"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .totalProductos(rs.getInt("total_productos"))
                .build();
    }
}