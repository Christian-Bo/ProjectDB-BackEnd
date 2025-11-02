package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.dto.clientes.ClienteCreateRequestDto;
import com.nexttechstore.nexttech_backend.dto.clientes.ClienteUpdateRequestDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ClientesCommandRepository {

    private final JdbcTemplate jdbc;

    public ClientesCommandRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // -------- helpers de normalización --------
    private String normEstado(String e) {
        if (e == null) return null;
        String x = e.trim().toUpperCase();
        if ("N".equals(x)) x = "I";              // sinónimo de Inactivo
        if (!"A".equals(x) && !"I".equals(x)) {  // deja que el SP valide si viene algo raro
            return x;
        }
        return x;
    }

    /** Para cumplir el CHECK constraint actual, siempre devolvemos "I". */
    private String tipoFijo() {
        return "I";
    }

    private BigDecimal toDecimal(BigDecimal v){ return v == null ? null : v; }
    private int nz(Integer v){ return v==null ? -1 : v; }
    private String nz(String v){ return v==null ? "" : v; }

    // -------- operaciones --------

    public ResultadoCrear crear(ClienteCreateRequestDto req) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_clientes_create")
                .declareParameters(
                        new SqlParameter("p_codigo",         Types.NVARCHAR),
                        new SqlParameter("p_nombre",         Types.NVARCHAR),
                        new SqlParameter("p_nit",            Types.NVARCHAR),
                        new SqlParameter("p_telefono",       Types.NVARCHAR),
                        new SqlParameter("p_direccion",      Types.NVARCHAR),
                        new SqlParameter("p_email",          Types.NVARCHAR),
                        new SqlParameter("p_limite_credito", Types.DECIMAL),
                        new SqlParameter("p_dias_credito",   Types.INTEGER),
                        new SqlParameter("p_estado",         Types.NCHAR),
                        new SqlParameter("p_tipo_cliente",   Types.NCHAR),
                        new SqlParameter("p_registrado_por", Types.INTEGER),

                        new SqlOutParameter("out_id",          Types.INTEGER),
                        new SqlOutParameter("out_status_code", Types.INTEGER),
                        new SqlOutParameter("out_message",     Types.NVARCHAR)
                );

        Map<String, Object> in = new HashMap<>();
        in.put("p_codigo",          req.codigo());
        in.put("p_nombre",          req.nombre());
        in.put("p_nit",             req.nit());
        in.put("p_telefono",        req.telefono());
        in.put("p_direccion",       req.direccion());
        in.put("p_email",           req.email());
        in.put("p_limite_credito",  toDecimal(req.limiteCredito()));
        in.put("p_dias_credito",    req.diasCredito());
        in.put("p_estado",          normEstado(req.estado()));
        in.put("p_tipo_cliente",    tipoFijo());           // <- SIEMPRE "I"
        in.put("p_registrado_por",  req.registradoPor());

        Map<String, Object> out = call.execute(in);
        Integer id    = (Integer) out.get("out_id");
        Integer code  = (Integer) out.get("out_status_code");
        String  msg   = (String)  out.get("out_message");
        return new ResultadoCrear(id, nz(code), nz(msg));
    }

    public ResultadoBasico actualizar(int id, ClienteUpdateRequestDto req) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_clientes_update")
                .declareParameters(
                        new SqlParameter("p_id",             Types.INTEGER),
                        new SqlParameter("p_codigo",         Types.NVARCHAR),
                        new SqlParameter("p_nombre",         Types.NVARCHAR),
                        new SqlParameter("p_nit",            Types.NVARCHAR),
                        new SqlParameter("p_telefono",       Types.NVARCHAR),
                        new SqlParameter("p_direccion",      Types.NVARCHAR),
                        new SqlParameter("p_email",          Types.NVARCHAR),
                        new SqlParameter("p_limite_credito", Types.DECIMAL),
                        new SqlParameter("p_dias_credito",   Types.INTEGER),
                        new SqlParameter("p_estado",         Types.NCHAR),
                        new SqlParameter("p_tipo_cliente",   Types.NCHAR),

                        new SqlOutParameter("out_status_code", Types.INTEGER),
                        new SqlOutParameter("out_message",    Types.NVARCHAR)
                );

        Map<String, Object> in = new HashMap<>();
        in.put("p_id",               id);
        in.put("p_codigo",           req.codigo());
        in.put("p_nombre",           req.nombre());
        in.put("p_nit",              req.nit());
        in.put("p_telefono",         req.telefono());
        in.put("p_direccion",        req.direccion());
        in.put("p_email",            req.email());
        in.put("p_limite_credito",   toDecimal(req.limiteCredito()));
        in.put("p_dias_credito",     req.diasCredito());
        in.put("p_estado",           normEstado(req.estado()));
        in.put("p_tipo_cliente",     tipoFijo());          // <- SIEMPRE "I"

        Map<String, Object> out = call.execute(in);
        Integer code  = (Integer) out.get("out_status_code");
        String  msg   = (String)  out.get("out_message");
        return new ResultadoBasico(nz(code), nz(msg));
    }

    public ResultadoBasico setEstado(int id, String estado) {
        String norm = normEstado(estado);
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_clientes_set_estado")
                .declareParameters(
                        new SqlParameter("p_id", Types.INTEGER),
                        new SqlParameter("p_estado", Types.NCHAR),
                        new SqlOutParameter("out_status_code", Types.INTEGER),
                        new SqlOutParameter("out_message", Types.NVARCHAR)
                );

        Map<String, Object> out = call.execute(Map.of(
                "p_id", id,
                "p_estado", norm
        ));
        Integer code  = (Integer) out.get("out_status_code");
        String  msg   = (String)  out.get("out_message");
        return new ResultadoBasico(nz(code), nz(msg));
    }

    // Result wrappers
    public record ResultadoCrear(Integer id, int code, String message) {}
    public record ResultadoBasico(int code, String message) {}
}
