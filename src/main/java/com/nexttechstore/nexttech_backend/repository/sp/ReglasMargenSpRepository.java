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

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReglasMargenSpRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApiResponse<ReglaMargenResponseDTO> create(ReglaMargenRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ReglaMargenCreate")
                    .declareParameters(
                            new SqlParameter("categoria_id", Types.INTEGER),
                            new SqlParameter("marca_id", Types.INTEGER),
                            new SqlParameter("margen_pct", Types.DECIMAL),
                            new SqlOutParameter("reglaId", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("categoria_id", request.getCategoriaId())
                    .addValue("marca_id", request.getMarcaId())
                    .addValue("margen_pct", request.getMargenPct());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);

                ReglaMargenResponseDTO regla = ReglaMargenResponseDTO.builder()
                        .id((Integer) row.get("id"))
                        .categoriaId((Integer) row.get("categoria_id"))
                        .categoriaNombre((String) row.get("categoria_nombre"))
                        .marcaId((Integer) row.get("marca_id"))
                        .marcaNombre((String) row.get("marca_nombre"))
                        .margenPct((java.math.BigDecimal) row.get("margen_pct"))
                        .build();

                return ApiResponse.success((String) row.get("mensaje"), regla);
            }

            return ApiResponse.error("No se pudo crear la regla");

        } catch (Exception e) {
            log.error("Error al crear regla de margen: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Void> update(Integer id, ReglaMargenRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ReglaMargenUpdate")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER),
                            new SqlParameter("categoria_id", Types.INTEGER),
                            new SqlParameter("marca_id", Types.INTEGER),
                            new SqlParameter("margen_pct", Types.DECIMAL)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("categoria_id", request.getCategoriaId())
                    .addValue("marca_id", request.getMarcaId())
                    .addValue("margen_pct", request.getMargenPct());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo actualizar la regla");

        } catch (Exception e) {
            log.error("Error al actualizar regla: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Void> delete(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ReglaMargenDelete")
                    .declareParameters(new SqlParameter("id", Types.INTEGER));

            MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo eliminar la regla");

        } catch (Exception e) {
            log.error("Error al eliminar regla: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<List<ReglaMargenResponseDTO>> getAll() {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ReglaMargenGetAll")
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToRegla(rs));

            Map<String, Object> result = jdbcCall.execute();

            @SuppressWarnings("unchecked")
            List<ReglaMargenResponseDTO> reglas = (List<ReglaMargenResponseDTO>) result.get("#result-set-1");

            if (reglas != null) {
                return ApiResponse.success("Reglas obtenidas", reglas, reglas.size());
            }

            return ApiResponse.error("No se pudieron obtener las reglas");

        } catch (Exception e) {
            log.error("Error al obtener reglas: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Map<String, Object>> aplicarMasivo(ReglaMargenAplicarMasivoRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ReglaMargenAplicarMasivo")
                    .declareParameters(
                            new SqlParameter("categoria_id", Types.INTEGER),
                            new SqlParameter("marca_id", Types.INTEGER),
                            new SqlParameter("solo_sin_precio_venta", Types.BIT)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("categoria_id", request.getCategoriaId())
                    .addValue("marca_id", request.getMarcaId())
                    .addValue("solo_sin_precio_venta", request.getSoloSinPrecioVenta());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                return ApiResponse.success("Reglas aplicadas masivamente", resultList.get(0));
            }

            return ApiResponse.error("No se pudieron aplicar las reglas");

        } catch (Exception e) {
            log.error("Error al aplicar reglas masivamente: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    private ReglaMargenResponseDTO mapRowToRegla(ResultSet rs) throws SQLException {
        return ReglaMargenResponseDTO.builder()
                .id(rs.getInt("id"))
                .categoriaId((Integer) rs.getObject("categoria_id"))
                .categoriaNombre(rs.getString("categoria_nombre"))
                .marcaId((Integer) rs.getObject("marca_id"))
                .marcaNombre(rs.getString("marca_nombre"))
                .margenPct(rs.getBigDecimal("margen_pct"))
                .tipoRegla(rs.getString("tipo_regla"))
                .prioridad(rs.getInt("prioridad"))
                .build();
    }
}