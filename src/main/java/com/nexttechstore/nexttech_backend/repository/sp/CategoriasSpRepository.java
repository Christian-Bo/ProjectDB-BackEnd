package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.catalogos.CategoriaArbolDTO;
import com.nexttechstore.nexttech_backend.dto.catalogos.CategoriaRequestDTO;
import com.nexttechstore.nexttech_backend.dto.catalogos.CategoriaResponseDTO;
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
public class CategoriasSpRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApiResponse<CategoriaResponseDTO> create(CategoriaRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CategoriaCreate")
                    .declareParameters(
                            new SqlParameter("nombre", Types.NVARCHAR),
                            new SqlParameter("descripcion", Types.NVARCHAR),
                            new SqlParameter("activo", Types.BIT),
                            new SqlParameter("categoria_padre_id", Types.INTEGER),
                            new SqlOutParameter("categoriaId", Types.INTEGER),
                            new SqlOutParameter("categoriaNombre", Types.NVARCHAR)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("nombre", request.getNombre())
                    .addValue("descripcion", request.getDescripcion())
                    .addValue("activo", request.getActivo())
                    .addValue("categoria_padre_id", request.getCategoriaPadreId());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);

                CategoriaResponseDTO categoria = CategoriaResponseDTO.builder()
                        .id((Integer) row.get("id"))
                        .nombre((String) row.get("nombre"))
                        .categoriaPadreId((Integer) row.get("categoria_padre_id"))
                        .nombrePadre((String) row.get("nombre_padre"))
                        .build();

                return ApiResponse.success(
                        (String) row.get("mensaje"),
                        categoria
                );
            }

            return ApiResponse.error("No se pudo crear la categoría");

        } catch (Exception e) {
            log.error("Error al crear categoría: ", e);
            return ApiResponse.error("Error al crear categoría: " + e.getMessage());
        }
    }

    public ApiResponse<Void> update(Integer id, CategoriaRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CategoriaUpdate")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER),
                            new SqlParameter("nombre", Types.NVARCHAR),
                            new SqlParameter("descripcion", Types.NVARCHAR),
                            new SqlParameter("activo", Types.BIT),
                            new SqlParameter("categoria_padre_id", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("nombre", request.getNombre())
                    .addValue("descripcion", request.getDescripcion())
                    .addValue("activo", request.getActivo())
                    .addValue("categoria_padre_id", request.getCategoriaPadreId());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo actualizar la categoría");

        } catch (Exception e) {
            log.error("Error al actualizar categoría: ", e);
            return ApiResponse.error("Error al actualizar categoría: " + e.getMessage());
        }
    }

    public ApiResponse<Void> delete(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CategoriaDelete")
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

            return ApiResponse.error("No se pudo eliminar la categoría");

        } catch (Exception e) {
            log.error("Error al eliminar categoría: ", e);
            return ApiResponse.error("Error al eliminar categoría: " + e.getMessage());
        }
    }

    public ApiResponse<Void> activate(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CategoriaActivar")
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

            return ApiResponse.error("No se pudo activar la categoría");

        } catch (Exception e) {
            log.error("Error al activar categoría: ", e);
            return ApiResponse.error("Error al activar categoría: " + e.getMessage());
        }
    }

    public Optional<CategoriaResponseDTO> getById(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CategoriaGetById")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToCategoriaResponse(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<CategoriaResponseDTO> categorias = (List<CategoriaResponseDTO>) result.get("#result-set-1");

            return categorias != null && !categorias.isEmpty()
                    ? Optional.of(categorias.get(0))
                    : Optional.empty();

        } catch (Exception e) {
            log.error("Error al obtener categoría por ID: ", e);
            return Optional.empty();
        }
    }

    public ApiResponse<List<CategoriaResponseDTO>> getAll(Boolean soloActivas, PageRequest pageRequest) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CategoriaGetAll")
                    .declareParameters(
                            new SqlParameter("soloActivas", Types.BIT)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToCategoriaResponse(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("soloActivas", soloActivas);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<CategoriaResponseDTO> categorias = (List<CategoriaResponseDTO>) result.get("#result-set-1");

            if (categorias != null) {
                return ApiResponse.success(
                        "Categorías obtenidas exitosamente",
                        categorias,
                        categorias.size()
                );
            }

            return ApiResponse.error("No se pudieron obtener las categorías");

        } catch (Exception e) {
            log.error("Error al obtener categorías: ", e);
            return ApiResponse.error("Error al obtener categorías: " + e.getMessage());
        }
    }

    /**
     * Obtener árbol completo de categorías (consulta recursiva)
     */
    public ApiResponse<List<CategoriaArbolDTO>> getArbolCompleto(Boolean soloActivas) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CategoriaGetArbolCompleto")
                    .declareParameters(
                            new SqlParameter("soloActivas", Types.BIT)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToArbolDTO(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("soloActivas", soloActivas);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<CategoriaArbolDTO> arbol = (List<CategoriaArbolDTO>) result.get("#result-set-1");

            if (arbol != null) {
                return ApiResponse.success(
                        "Árbol de categorías obtenido exitosamente",
                        arbol,
                        arbol.size()
                );
            }

            return ApiResponse.error("No se pudo obtener el árbol de categorías");

        } catch (Exception e) {
            log.error("Error al obtener árbol de categorías: ", e);
            return ApiResponse.error("Error al obtener árbol de categorías: " + e.getMessage());
        }
    }

    /**
     * Obtener subcategorías de una categoría padre
     */
    public ApiResponse<List<CategoriaResponseDTO>> getHijas(Integer categoriaPadreId, Boolean soloActivas) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CategoriaGetHijas")
                    .declareParameters(
                            new SqlParameter("categoria_padre_id", Types.INTEGER),
                            new SqlParameter("soloActivas", Types.BIT)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToCategoriaResponse(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("categoria_padre_id", categoriaPadreId)
                    .addValue("soloActivas", soloActivas);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<CategoriaResponseDTO> categorias = (List<CategoriaResponseDTO>) result.get("#result-set-1");

            if (categorias != null) {
                return ApiResponse.success(
                        "Subcategorías obtenidas exitosamente",
                        categorias,
                        categorias.size()
                );
            }

            return ApiResponse.error("No se pudieron obtener las subcategorías");

        } catch (Exception e) {
            log.error("Error al obtener subcategorías: ", e);
            return ApiResponse.error("Error al obtener subcategorías: " + e.getMessage());
        }
    }

    private CategoriaResponseDTO mapRowToCategoriaResponse(ResultSet rs) throws SQLException {
        return CategoriaResponseDTO.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .activo(rs.getBoolean("activo"))
                .categoriaPadreId((Integer) rs.getObject("categoria_padre_id"))
                .nombrePadre(rs.getString("nombre_padre"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .totalProductos(rs.getInt("total_productos"))
                .totalSubcategorias(rs.getInt("total_subcategorias"))
                .build();
    }

    private CategoriaArbolDTO mapRowToArbolDTO(ResultSet rs) throws SQLException {
        return CategoriaArbolDTO.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .activo(rs.getBoolean("activo"))
                .categoriaPadreId((Integer) rs.getObject("categoria_padre_id"))
                .ruta(rs.getString("ruta"))
                .nivel(rs.getInt("nivel"))
                .nombreIndentado(rs.getString("nombre_indentado"))
                .build();
    }
}