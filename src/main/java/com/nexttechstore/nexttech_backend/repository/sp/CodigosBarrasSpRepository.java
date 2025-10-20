package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.catalogos.CodigoBarrasRequestDTO;
import com.nexttechstore.nexttech_backend.dto.catalogos.CodigoBarrasResponseDTO;
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

@Repository
@RequiredArgsConstructor
@Slf4j
public class CodigosBarrasSpRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApiResponse<CodigoBarrasResponseDTO> create(CodigoBarrasRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CodigoBarrasCreate")
                    .declareParameters(
                            new SqlParameter("producto_id", Types.INTEGER),
                            new SqlParameter("codigo_barras", Types.NVARCHAR),
                            new SqlParameter("tipo_codigo", Types.CHAR),
                            new SqlParameter("activo", Types.BIT),
                            new SqlOutParameter("codigoId", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("producto_id", request.getProductoId())
                    .addValue("codigo_barras", request.getCodigoBarras())
                    .addValue("tipo_codigo", request.getTipoCodigo())
                    .addValue("activo", request.getActivo());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);

                CodigoBarrasResponseDTO codigo = CodigoBarrasResponseDTO.builder()
                        .id((Integer) row.get("id"))
                        .codigoBarras((String) row.get("codigo_barras"))
                        .productoId((Integer) row.get("producto_id"))
                        .productoNombre((String) row.get("producto_nombre"))
                        .build();

                return ApiResponse.success((String) row.get("mensaje"), codigo);
            }

            return ApiResponse.error("No se pudo crear el código de barras");

        } catch (Exception e) {
            log.error("Error al crear código de barras: ", e);
            return ApiResponse.error("Error al crear código de barras: " + e.getMessage());
        }
    }

    public ApiResponse<Void> update(Integer id, CodigoBarrasRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CodigoBarrasUpdate")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER),
                            new SqlParameter("codigo_barras", Types.NVARCHAR),
                            new SqlParameter("tipo_codigo", Types.CHAR),
                            new SqlParameter("activo", Types.BIT)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("codigo_barras", request.getCodigoBarras())
                    .addValue("tipo_codigo", request.getTipoCodigo())
                    .addValue("activo", request.getActivo());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo actualizar el código de barras");

        } catch (Exception e) {
            log.error("Error al actualizar código de barras: ", e);
            return ApiResponse.error("Error al actualizar código de barras: " + e.getMessage());
        }
    }

    public ApiResponse<Void> delete(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CodigoBarrasDelete")
                    .declareParameters(new SqlParameter("id", Types.INTEGER));

            MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo eliminar el código de barras");

        } catch (Exception e) {
            log.error("Error al eliminar código de barras: ", e);
            return ApiResponse.error("Error al eliminar código de barras: " + e.getMessage());
        }
    }

    public ApiResponse<List<CodigoBarrasResponseDTO>> getByProducto(Integer productoId, Boolean soloActivos) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_CodigoBarrasGetByProducto")
                    .declareParameters(
                            new SqlParameter("producto_id", Types.INTEGER),
                            new SqlParameter("soloActivos", Types.BIT)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToCodigoBarras(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("producto_id", productoId)
                    .addValue("soloActivos", soloActivos);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<CodigoBarrasResponseDTO> codigos = (List<CodigoBarrasResponseDTO>) result.get("#result-set-1");

            if (codigos != null) {
                return ApiResponse.success("Códigos de barras obtenidos exitosamente", codigos, codigos.size());
            }

            return ApiResponse.error("No se pudieron obtener los códigos de barras");

        } catch (Exception e) {
            log.error("Error al obtener códigos de barras: ", e);
            return ApiResponse.error("Error al obtener códigos de barras: " + e.getMessage());
        }
    }

    private CodigoBarrasResponseDTO mapRowToCodigoBarras(ResultSet rs) throws SQLException {
        return CodigoBarrasResponseDTO.builder()
                .id(rs.getInt("id"))
                .productoId(rs.getInt("producto_id"))
                .productoCodigo(rs.getString("producto_codigo"))
                .productoNombre(rs.getString("producto_nombre"))
                .codigoBarras(rs.getString("codigo_barras"))
                .tipoCodigo(rs.getString("tipo_codigo"))
                .tipoCodigoNombre(rs.getString("tipo_codigo_nombre"))
                .activo(rs.getBoolean("activo"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .build();
    }
}
