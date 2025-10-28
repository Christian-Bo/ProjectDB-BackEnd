package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ClienteListaPreciosSpRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApiResponse<ClienteListaPreciosResponseDTO> asignarLista(ClienteAsignarListaRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ClienteAsignarListaPrecios")
                    .declareParameters(
                            new SqlParameter("cliente_id", Types.INTEGER),
                            new SqlParameter("lista_id", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("cliente_id", request.getClienteId())
                    .addValue("lista_id", request.getListaId());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);

                ClienteListaPreciosResponseDTO response = ClienteListaPreciosResponseDTO.builder()
                        .clienteId((Integer) row.get("cliente_id"))
                        .clienteCodigo((String) row.get("cliente_codigo"))
                        .clienteNombre((String) row.get("cliente_nombre"))
                        .listaId((Integer) row.get("lista_id"))
                        .listaNombre((String) row.get("lista_nombre"))
                        .build();

                return ApiResponse.success((String) row.get("mensaje"), response);
            }

            return ApiResponse.error("No se pudo asignar la lista");

        } catch (Exception e) {
            log.error("Error al asignar lista a cliente: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public ApiResponse<Void> removerLista(Integer clienteId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ClienteRemoverListaPrecios")
                    .declareParameters(new SqlParameter("cliente_id", Types.INTEGER));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("cliente_id", clienteId);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo remover la lista");

        } catch (Exception e) {
            log.error("Error al remover lista de cliente: ", e);
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    public Optional<ClienteListaPreciosResponseDTO> getListaByCliente(Integer clienteId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ClienteGetListaPrecios")
                    .declareParameters(new SqlParameter("cliente_id", Types.INTEGER))
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToClienteLista(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("cliente_id", clienteId);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<ClienteListaPreciosResponseDTO> listas = (List<ClienteListaPreciosResponseDTO>) result.get("#result-set-1");

            return listas != null && !listas.isEmpty() ? Optional.of(listas.get(0)) : Optional.empty();

        } catch (Exception e) {
            log.error("Error al obtener lista de cliente: ", e);
            return Optional.empty();
        }
    }

    public Optional<ClientePrecioProductoResponseDTO> getPrecioProducto(Integer clienteId, Integer productoId, String fecha) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ClienteGetPrecioProducto")
                    .declareParameters(
                            new SqlParameter("cliente_id", Types.INTEGER),
                            new SqlParameter("producto_id", Types.INTEGER),
                            new SqlParameter("fecha", Types.DATE)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> {
                        return ClientePrecioProductoResponseDTO.builder()
                                .precio(rs.getBigDecimal("precio"))
                                .origen(rs.getString("origen"))
                                .mensaje(rs.getString("mensaje"))
                                .build();
                    });

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("cliente_id", clienteId)
                    .addValue("producto_id", productoId)
                    .addValue("fecha", fecha);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<ClientePrecioProductoResponseDTO> precios = (List<ClientePrecioProductoResponseDTO>) result.get("#result-set-1");

            return precios != null && !precios.isEmpty() ? Optional.of(precios.get(0)) : Optional.empty();

        } catch (Exception e) {
            log.error("Error al obtener precio de producto para cliente: ", e);
            return Optional.empty();
        }
    }

    private ClienteListaPreciosResponseDTO mapRowToClienteLista(ResultSet rs) throws SQLException {
        return ClienteListaPreciosResponseDTO.builder()
                .clienteId(rs.getInt("cliente_id"))
                .clienteCodigo(rs.getString("cliente_codigo"))
                .clienteNombre(rs.getString("cliente_nombre"))
                .listaId((Integer) rs.getObject("lista_id"))
                .listaNombre(rs.getString("lista_nombre"))
                .moneda(rs.getString("moneda"))
                .activa((Boolean) rs.getObject("activa"))
                .fechaAsignacion(rs.getTimestamp("fecha_asignacion") != null ?
                        rs.getTimestamp("fecha_asignacion").toLocalDateTime() : null)
                .build();
    }
}