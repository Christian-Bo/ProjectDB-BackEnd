// src/main/java/com/nexttechstore/nexttech_backend/repository/orm/DevolucionesCommandRepository.java
package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.dto.devoluciones.DevolucionCreateRequestDto;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DevolucionesCommandRepository {

    private final JdbcTemplate jdbc;

    public DevolucionesCommandRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Ejecuta sp_devoluciones_create usando TVP dbo.tvp_devolucion_detalle_v1 */
    public ResultadoCrear crear(DevolucionCreateRequestDto req) throws Exception {
        // Construir el TVP en memoria
        SQLServerDataTable tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("detalle_venta_id", Types.INTEGER);
        tvp.addColumnMetadata("producto_id",     Types.INTEGER);
        tvp.addColumnMetadata("cantidad",        Types.INTEGER);
        tvp.addColumnMetadata("observaciones",   Types.NVARCHAR);

        for (var it : req.items()) {
            tvp.addRow(it.detalleVentaId(), it.productoId(), it.cantidad(), it.observaciones());
        }

        // (Opcional) Nombre del tipo en el driver:
        try { tvp.setTvpName("dbo.tvp_devolucion_detalle_v1"); } catch (Throwable ignore) {}

        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_devoluciones_create")
                .declareParameters(
                        new SqlParameter("p_venta_id", Types.INTEGER),
                        new SqlParameter("p_fecha",    Types.DATE),
                        new SqlParameter("p_motivo",   Types.NVARCHAR),
                        // IMPORTANTE: usar microsoft.sql.Types.STRUCTURED (no java.sql.Types)
                        new SqlParameter("p_detalle",  microsoft.sql.Types.STRUCTURED),
                        new SqlOutParameter("out_devolucion_id", Types.INTEGER),
                        new SqlOutParameter("out_status_code",   Types.INTEGER),
                        new SqlOutParameter("out_message",       Types.NVARCHAR)
                );

        Map<String, Object> in = new HashMap<>();
        in.put("p_venta_id", req.ventaId());
        in.put("p_fecha",    req.fecha());
        in.put("p_motivo",   req.motivo());

        // A) Si el driver respeta setTvpName:
        // in.put("p_detalle", tvp);

        // B) Pasando el nombre del tipo aquí (seguro para todos los drivers MS):
        SqlParameterValue tvpParam = new SqlParameterValue(
                microsoft.sql.Types.STRUCTURED,
                "dbo.tvp_devolucion_detalle_v1",
                tvp
        );
        in.put("p_detalle", tvpParam);

        Map<String, Object> out = call.execute(in);

        Integer code   = (Integer) out.get("out_status_code");
        String  msg    = (String)  out.get("out_message");
        Integer devId  = (Integer) out.get("out_devolucion_id");

        return new ResultadoCrear(code == null ? -1 : code, msg, devId);
    }

    /** (Opcional) Anular devolución si creaste el SP sp_devoluciones_annul */
    public ResultadoBasico anular(int devolucionId, String motivoAnulacion) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_devoluciones_annul")
                .declareParameters(
                        new SqlParameter("p_id", Types.INTEGER),
                        new SqlParameter("p_motivo", Types.NVARCHAR),
                        new SqlOutParameter("out_status_code", Types.INTEGER),
                        new SqlOutParameter("out_message",     Types.NVARCHAR)
                );

        Map<String, Object> out = call.execute(Map.of(
                "p_id", devolucionId,
                "p_motivo", motivoAnulacion
        ));

        Integer code = (Integer) out.get("out_status_code");
        String  msg  = (String)  out.get("out_message");
        return new ResultadoBasico(code == null ? -1 : code, msg);
    }

    // ===== Result wrappers =====
    public record ResultadoCrear(int code, String message, Integer devolucionId) {}
    public record ResultadoBasico(int code, String message) {}
}
