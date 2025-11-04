package com.nexttechstore.nexttech_backend.repository.orm;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Repository
public class FacturasCommandRepository {

    private final JdbcTemplate jdbc;

    public FacturasCommandRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Llama a dbo.sp_facturas_emitir
     * Parámetros IN:  p_venta_id, p_serie_id, p_emitida_por
     * Parámetros OUT: out_factura_id, out_status_code, out_message
     */
    public ResultadoEmitir emitirFactura(int ventaId, int serieId, int emitidaPor) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                    .withSchemaName("dbo")
                    .withProcedureName("sp_facturas_emitir")
                    // Evita introspección de metadata (en SQL Server suele ser más robusto declarar todo)
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("p_venta_id", Types.INTEGER),
                            new SqlParameter("p_serie_id", Types.INTEGER),
                            new SqlParameter("p_emitida_por", Types.INTEGER),
                            new SqlOutParameter("out_factura_id", Types.INTEGER),
                            new SqlOutParameter("out_status_code", Types.INTEGER),
                            new SqlOutParameter("out_message", Types.NVARCHAR)
                    );

            Map<String,Object> in = new HashMap<>();
            in.put("p_venta_id", ventaId);
            in.put("p_serie_id", serieId);
            in.put("p_emitida_por", emitidaPor);

            Map<String,Object> out = call.execute(in);

            Integer code = (Integer) out.get("out_status_code");
            String message = (String) out.get("out_message");
            Integer facturaId = (Integer) out.get("out_factura_id");

            return new ResultadoEmitir(code == null ? -1 : code, message, facturaId);
        } catch (Exception e) {
            // Para debug rápido desde el controller/servicio
            throw new RuntimeException("Error llamando sp_facturas_emitir: " + e.getMessage(), e);
        }
    }

    /** (Fase 2) anular factura: placeholder */
    public ResultadoBasico anularFactura(int facturaId, String motivo, int usuarioId) {
        // Si luego creas sp_facturas_anular, lo llamas aquí con SimpleJdbcCall.
        return new ResultadoBasico(0, "OK");
    }

    // DTOs simples (records)
    public record ResultadoEmitir(int code, String message, Integer facturaId) {}
    public record ResultadoBasico(int code, String message) {}
}
