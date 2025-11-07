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
public class TransferenciaSpRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> listarTransferencias(Integer bodegaOrigenId, Integer bodegaDestinoId, String estado) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_transferencias_listar")
                .declareParameters(
                        new SqlParameter("bodega_origen_id", Types.INTEGER),
                        new SqlParameter("bodega_destino_id", Types.INTEGER),
                        new SqlParameter("estado", Types.CHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("bodega_origen_id", bodegaOrigenId);
        params.put("bodega_destino_id", bodegaDestinoId);
        params.put("estado", estado);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public Map<String, Object> obtenerTransferenciaPorId(Integer id) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_transferencia_obtener_por_id")
                .declareParameters(new SqlParameter("id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> lista = (List<Map<String, Object>>) result.get("#result-set-1");

        return lista.isEmpty() ? null : lista.get(0);
    }

    public List<Map<String, Object>> obtenerDetalleTransferencia(Integer transferenciaId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_transferencia_detalle_listar")
                .declareParameters(new SqlParameter("transferencia_id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("transferencia_id", transferenciaId);

        Map<String, Object> result = jdbcCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    public Map<String, Object> crearTransferencia(String numeroTransferencia, String fechaTransferencia,
                                                  Integer bodegaOrigenId, Integer bodegaDestinoId,
                                                  Integer solicitanteId, String observaciones, String detallesJson) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_transferencia_crear")
                .declareParameters(
                        new SqlParameter("numero_transferencia", Types.NVARCHAR),
                        new SqlParameter("fecha_transferencia", Types.DATE),
                        new SqlParameter("bodega_origen_id", Types.INTEGER),
                        new SqlParameter("bodega_destino_id", Types.INTEGER),
                        new SqlParameter("solicitado_por", Types.INTEGER),
                        new SqlParameter("observaciones", Types.NVARCHAR),
                        new SqlParameter("detalles", Types.NVARCHAR)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("numero_transferencia", numeroTransferencia);
        params.put("fecha_transferencia", fechaTransferencia);
        params.put("bodega_origen_id", bodegaOrigenId);
        params.put("bodega_destino_id", bodegaDestinoId);
        params.put("solicitado_por", solicitanteId);
        params.put("observaciones", observaciones);
        params.put("detalles", detallesJson);

        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> lista = (List<Map<String, Object>>) result.get("#result-set-1");

        return lista.isEmpty() ? null : lista.get(0);
    }

    public void aprobarTransferencia(Integer id, Integer aprobadorId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_transferencia_aprobar")
                .declareParameters(
                        new SqlParameter("id", Types.INTEGER),
                        new SqlParameter("aprobador_id", Types.INTEGER)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("aprobador_id", aprobadorId);

        jdbcCall.execute(params);
    }

    public void recibirTransferencia(Integer id, Integer receptorId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_transferencia_recibir")
                .declareParameters(
                        new SqlParameter("id", Types.INTEGER),
                        new SqlParameter("receptor_id", Types.INTEGER)
                );

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("receptor_id", receptorId);

        jdbcCall.execute(params);
    }

    public void cancelarTransferencia(Integer id) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_transferencia_cancelar")
                .declareParameters(new SqlParameter("id", Types.INTEGER));

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        jdbcCall.execute(params);
    }
}