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
public class CotizacionesCommandRepository {

    private final JdbcTemplate jdbc;

    public CotizacionesCommandRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Llama a sp_cotizaciones_create (ya lo tienes) */
    public Map<String, Object> crearCotizacion(
            Integer clienteId, Integer vendedorId, String fechaVigencia,
            String observaciones, String terminos, Number descGeneral, Number iva,
            String jsonDetalle // si usas TVP vía jackson, etc. omite si ya lo implementaste
    ) {
        // Deja el cuerpo como ya lo tenías. Este método se incluye solo como placeholder.
        return Map.of();
    }

    /** Convierte cotización a venta (crea la venta). Siempre crea como contado (por el SP). */
    public ResultadoToVenta cotizacionToVenta(int cotizacionId, int bodegaId, int serieId, Integer cajeroId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_cotizaciones_to_venta")
                .declareParameters(
                        new SqlParameter("p_cotizacion_id", Types.INTEGER),
                        new SqlParameter("p_bodega_origen_id", Types.INTEGER),
                        new SqlParameter("p_serie_id", Types.INTEGER),
                        new SqlParameter("p_cajero_id", Types.INTEGER),
                        new SqlOutParameter("out_venta_id", Types.INTEGER),
                        new SqlOutParameter("out_status_code", Types.INTEGER),
                        new SqlOutParameter("out_message", Types.NVARCHAR)
                );

        Map<String, Object> in = new HashMap<>();
        in.put("p_cotizacion_id", cotizacionId);
        in.put("p_bodega_origen_id", bodegaId);
        in.put("p_serie_id", serieId);
        in.put("p_cajero_id", cajeroId);

        Map<String, Object> out = call.execute(in);

        Integer code = (Integer) out.get("out_status_code");
        String message = (String) out.get("out_message");
        Integer ventaId = (Integer) out.get("out_venta_id");

        return new ResultadoToVenta(code == null ? -1 : code, message, ventaId);
    }

    /** Ajusta el tipo de pago de la venta usando sp_ventas_edit_header (sin tocar schema). */
    public ResultadoBasico ventaEditarTipoPago(int ventaId, char tipoPago, Integer cajeroId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_ventas_edit_header")
                .declareParameters(
                        new SqlParameter("p_venta_id", Types.INTEGER),
                        new SqlParameter("p_cliente_id", Types.INTEGER),
                        new SqlParameter("p_tipo_pago", Types.CHAR),   // 'C' o 'R'
                        new SqlParameter("p_vendedor_id", Types.INTEGER),
                        new SqlParameter("p_cajero_id", Types.INTEGER),
                        new SqlParameter("p_observaciones", Types.NVARCHAR),
                        new SqlOutParameter("out_status_code", Types.INTEGER),
                        new SqlOutParameter("out_message", Types.NVARCHAR)
                );

        Map<String, Object> in = new HashMap<>();
        in.put("p_venta_id", ventaId);
        in.put("p_cliente_id", null);
        in.put("p_tipo_pago", String.valueOf(tipoPago));
        in.put("p_vendedor_id", null);
        in.put("p_cajero_id", cajeroId);       // opcional, puede ir null
        in.put("p_observaciones", null);

        Map<String, Object> out = call.execute(in);

        Integer code = (Integer) out.get("out_status_code");
        String message = (String) out.get("out_message");

        return new ResultadoBasico(code == null ? -1 : code, message);
    }

    // ===== Helper DTOs =====
    public record ResultadoToVenta(int code, String message, Integer ventaId) {}
    public record ResultadoBasico(int code, String message) {}
}
