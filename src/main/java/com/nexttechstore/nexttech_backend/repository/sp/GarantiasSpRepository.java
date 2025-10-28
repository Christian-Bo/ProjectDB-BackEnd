package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.productos.GarantiaRequestDTO;
import com.nexttechstore.nexttech_backend.dto.productos.GarantiaResponseDTO;
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
public class GarantiasSpRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApiResponse<GarantiaResponseDTO> create(GarantiaRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_GarantiaCreate")
                    .declareParameters(
                            new SqlParameter("venta_id", Types.INTEGER),
                            new SqlParameter("detalle_venta_id", Types.INTEGER),
                            new SqlParameter("numero_serie", Types.NVARCHAR),
                            new SqlParameter("fecha_inicio", Types.DATE),
                            new SqlParameter("fecha_vencimiento", Types.DATE),
                            new SqlParameter("estado", Types.CHAR),
                            new SqlParameter("observaciones", Types.NVARCHAR),
                            new SqlOutParameter("garantiaId", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("venta_id", request.getVentaId())
                    .addValue("detalle_venta_id", request.getDetalleVentaId())
                    .addValue("numero_serie", request.getNumeroSerie())
                    .addValue("fecha_inicio", request.getFechaInicio())
                    .addValue("fecha_vencimiento", request.getFechaVencimiento())
                    .addValue("estado", request.getEstado())
                    .addValue("observaciones", request.getObservaciones());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);

                GarantiaResponseDTO garantia = GarantiaResponseDTO.builder()
                        .id((Integer) row.get("id"))
                        .numeroSerie((String) row.get("numero_serie"))
                        .build();

                return ApiResponse.success((String) row.get("mensaje"), garantia);
            }

            return ApiResponse.error("No se pudo crear la garantía");

        } catch (Exception e) {
            log.error("Error al crear garantía: ", e);
            return ApiResponse.error("Error al crear garantía: " + e.getMessage());
        }
    }

    public ApiResponse<Void> update(Integer id, GarantiaRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_GarantiaUpdate")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER),
                            new SqlParameter("numero_serie", Types.NVARCHAR),
                            new SqlParameter("fecha_inicio", Types.DATE),
                            new SqlParameter("fecha_vencimiento", Types.DATE),
                            new SqlParameter("estado", Types.CHAR),
                            new SqlParameter("observaciones", Types.NVARCHAR)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("numero_serie", request.getNumeroSerie())
                    .addValue("fecha_inicio", request.getFechaInicio())
                    .addValue("fecha_vencimiento", request.getFechaVencimiento())
                    .addValue("estado", request.getEstado())
                    .addValue("observaciones", request.getObservaciones());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo actualizar la garantía");

        } catch (Exception e) {
            log.error("Error al actualizar garantía: ", e);
            return ApiResponse.error("Error al actualizar garantía: " + e.getMessage());
        }
    }

    public Optional<GarantiaResponseDTO> getById(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_GarantiaGetById")
                    .declareParameters(new SqlParameter("id", Types.INTEGER))
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToGarantia(rs));

            MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<GarantiaResponseDTO> garantias = (List<GarantiaResponseDTO>) result.get("#result-set-1");

            return garantias != null && !garantias.isEmpty() ? Optional.of(garantias.get(0)) : Optional.empty();

        } catch (Exception e) {
            log.error("Error al obtener garantía por ID: ", e);
            return Optional.empty();
        }
    }

    public ApiResponse<List<GarantiaResponseDTO>> getVigentes(Integer diasAlerta) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_GarantiaGetVigentes")
                    .declareParameters(new SqlParameter("dias_alerta", Types.INTEGER))
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToGarantia(rs));

            MapSqlParameterSource params = new MapSqlParameterSource().addValue("dias_alerta", diasAlerta);
            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<GarantiaResponseDTO> garantias = (List<GarantiaResponseDTO>) result.get("#result-set-1");

            if (garantias != null) {
                return ApiResponse.success("Garantías vigentes obtenidas", garantias, garantias.size());
            }

            return ApiResponse.error("No se pudieron obtener las garantías");

        } catch (Exception e) {
            log.error("Error al obtener garantías vigentes: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<List<GarantiaResponseDTO>> getByCliente(Integer clienteId, Boolean soloVigentes) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_GarantiaGetByCliente")
                    .declareParameters(
                            new SqlParameter("cliente_id", Types.INTEGER),
                            new SqlParameter("solo_vigentes", Types.BIT)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToGarantia(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("cliente_id", clienteId)
                    .addValue("solo_vigentes", soloVigentes);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<GarantiaResponseDTO> garantias = (List<GarantiaResponseDTO>) result.get("#result-set-1");

            if (garantias != null) {
                return ApiResponse.success("Garantías del cliente obtenidas", garantias, garantias.size());
            }

            return ApiResponse.error("No se pudieron obtener las garantías");

        } catch (Exception e) {
            log.error("Error al obtener garantías por cliente: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Void> marcarUsada(Integer id, String observaciones) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_GarantiaMarcarUsada")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER),
                            new SqlParameter("observaciones", Types.NVARCHAR)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("observaciones", observaciones);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo marcar la garantía como usada");

        } catch (Exception e) {
            log.error("Error al marcar garantía como usada: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    private GarantiaResponseDTO mapRowToGarantia(ResultSet rs) throws SQLException {
        return GarantiaResponseDTO.builder()
                .id(rs.getInt("id"))
                .ventaId(rs.getInt("venta_id"))
                .numeroVenta(rs.getString("numero_venta"))
                .detalleVentaId((Integer) rs.getObject("detalle_venta_id"))
                .productoCodigo(rs.getString("producto_codigo"))
                .productoNombre(rs.getString("producto_nombre"))
                .numeroSerie(rs.getString("numero_serie"))
                .fechaInicio(rs.getDate("fecha_inicio").toLocalDate())
                .fechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate())
                .estado(rs.getString("estado"))
                .estadoNombre(rs.getString("estado_nombre"))
                .observaciones(rs.getString("observaciones"))
                .clienteCodigo(rs.getString("cliente_codigo"))
                .clienteNombre(rs.getString("cliente_nombre"))
                .clienteTelefono(rs.getString("cliente_telefono"))
                .diasRestantes(rs.getInt("dias_restantes"))
                .build();
    }
}