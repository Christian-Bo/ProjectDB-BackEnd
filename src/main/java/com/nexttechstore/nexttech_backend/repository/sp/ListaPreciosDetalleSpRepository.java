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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ListaPreciosDetalleSpRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApiResponse<ListaPreciosDetalleResponseDTO> create(ListaPreciosDetalleRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosDetalleCreate")
                    .declareParameters(
                            new SqlParameter("lista_id", Types.INTEGER),
                            new SqlParameter("producto_id", Types.INTEGER),
                            new SqlParameter("precio", Types.DECIMAL),
                            new SqlParameter("vigente_desde", Types.DATE),
                            new SqlParameter("vigente_hasta", Types.DATE),
                            new SqlOutParameter("detalleId", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("lista_id", request.getListaId())
                    .addValue("producto_id", request.getProductoId())
                    .addValue("precio", request.getPrecio())
                    .addValue("vigente_desde", request.getVigenteDesde())
                    .addValue("vigente_hasta", request.getVigenteHasta());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);

                ListaPreciosDetalleResponseDTO detalle = ListaPreciosDetalleResponseDTO.builder()
                        .id((Integer) row.get("id"))
                        .listaId((Integer) row.get("lista_id"))
                        .listaNombre((String) row.get("lista_nombre"))
                        .productoId((Integer) row.get("producto_id"))
                        .productoNombre((String) row.get("producto_nombre"))
                        .precioLista((BigDecimal) row.get("precio"))
                        .build();

                return ApiResponse.success((String) row.get("mensaje"), detalle);
            }

            return ApiResponse.error("No se pudo crear el detalle");

        } catch (Exception e) {
            log.error("Error al crear detalle: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Void> update(Integer id, ListaPreciosDetalleRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosDetalleUpdate")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER),
                            new SqlParameter("precio", Types.DECIMAL),
                            new SqlParameter("vigente_desde", Types.DATE),
                            new SqlParameter("vigente_hasta", Types.DATE)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("precio", request.getPrecio())
                    .addValue("vigente_desde", request.getVigenteDesde())
                    .addValue("vigente_hasta", request.getVigenteHasta());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo actualizar el detalle");

        } catch (Exception e) {
            log.error("Error al actualizar detalle: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Void> delete(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosDetalleDelete")
                    .declareParameters(new SqlParameter("id", Types.INTEGER));

            MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo eliminar el detalle");

        } catch (Exception e) {
            log.error("Error al eliminar detalle: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<List<ListaPreciosDetalleResponseDTO>> getByLista(Integer listaId, Boolean soloVigentes) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosDetalleGetByLista")
                    .declareParameters(
                            new SqlParameter("lista_id", Types.INTEGER),
                            new SqlParameter("soloVigentes", Types.BIT)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToDetalle(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("lista_id", listaId)
                    .addValue("soloVigentes", soloVigentes);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<ListaPreciosDetalleResponseDTO> detalles = (List<ListaPreciosDetalleResponseDTO>) result.get("#result-set-1");

            if (detalles != null) {
                return ApiResponse.success("Detalles obtenidos", detalles, detalles.size());
            }

            return ApiResponse.error("No se pudieron obtener los detalles");

        } catch (Exception e) {
            log.error("Error al obtener detalles: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Map<String, Object>> aplicarIncremento(ListaPreciosIncrementoRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ListaPreciosAplicarIncremento")
                    .declareParameters(
                            new SqlParameter("lista_id", Types.INTEGER),
                            new SqlParameter("porcentaje_incremento", Types.DECIMAL),
                            new SqlParameter("vigente_desde", Types.DATE),
                            new SqlParameter("categoria_id", Types.INTEGER),
                            new SqlParameter("marca_id", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("lista_id", request.getListaId())
                    .addValue("porcentaje_incremento", request.getPorcentajeIncremento())
                    .addValue("vigente_desde", request.getVigenteDesde())
                    .addValue("categoria_id", request.getCategoriaId())
                    .addValue("marca_id", request.getMarcaId());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                return ApiResponse.success("Incremento aplicado", resultList.get(0));
            }

            return ApiResponse.error("No se pudo aplicar el incremento");

        } catch (Exception e) {
            log.error("Error al aplicar incremento: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    private ListaPreciosDetalleResponseDTO mapRowToDetalle(ResultSet rs) throws SQLException {
        return ListaPreciosDetalleResponseDTO.builder()
                .id(rs.getInt("id"))
                .listaId(rs.getInt("lista_id"))
                .listaNombre(rs.getString("lista_nombre"))
                .productoId(rs.getInt("producto_id"))
                .productoCodigo(rs.getString("producto_codigo"))
                .productoNombre(rs.getString("producto_nombre"))
                .precioBase(rs.getBigDecimal("precio_base"))
                .precioLista(rs.getBigDecimal("precio_lista"))
                .vigenteDesde(rs.getDate("vigente_desde").toLocalDate())
                .vigenteHasta(rs.getDate("vigente_hasta") != null ? rs.getDate("vigente_hasta").toLocalDate() : null)
                .estaVigente(rs.getBoolean("esta_vigente"))
                .build();
    }
}