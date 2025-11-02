package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.dto.precios.PrecioEspecialCreateRequestDto;
import com.nexttechstore.nexttech_backend.dto.precios.PrecioEspecialUpdateRequestDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Repository
public class PreciosEspecialesCommandRepository {

    private final JdbcTemplate jdbc;

    public PreciosEspecialesCommandRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Integer bit(Boolean b) {
        if (b == null) return null;
        return b ? 1 : 0;
    }
    private Date toDate(LocalDate d) { return d == null ? null : Date.valueOf(d); }
    private BigDecimal toDecimal(BigDecimal v){ return v == null ? null : v; }
    private int nz(Integer v){ return v==null ? -1 : v; }
    private String nz(String v){ return v==null ? "" : v; }

    // ----- crear -----
    public ResultadoCrear crear(PrecioEspecialCreateRequestDto req) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_precios_especiales_create")
                .declareParameters(
                        new SqlParameter("p_cliente_id", Types.INTEGER),
                        new SqlParameter("p_producto_id", Types.INTEGER),
                        new SqlParameter("p_precio_especial", Types.DECIMAL),
                        new SqlParameter("p_descuento_porcentaje", Types.DECIMAL),
                        new SqlParameter("p_fecha_inicio", Types.DATE),
                        new SqlParameter("p_fecha_vencimiento", Types.DATE),
                        new SqlParameter("p_activo", Types.BIT),
                        new SqlParameter("p_creado_por", Types.INTEGER),

                        new SqlOutParameter("out_id", Types.INTEGER),
                        new SqlOutParameter("out_status_code", Types.INTEGER),
                        new SqlOutParameter("out_message", Types.NVARCHAR)
                );

        Map<String, Object> in = new HashMap<>();
        in.put("p_cliente_id",           req.clienteId());
        in.put("p_producto_id",          req.productoId());
        in.put("p_precio_especial",      toDecimal(req.precioEspecial()));
        in.put("p_descuento_porcentaje", toDecimal(req.descuentoPorcentaje()));
        in.put("p_fecha_inicio",         toDate(req.fechaInicio()));
        in.put("p_fecha_vencimiento",    toDate(req.fechaVencimiento()));
        in.put("p_activo",               bit(req.activo() == null ? Boolean.TRUE : req.activo()));
        in.put("p_creado_por",           req.creadoPor());

        Map<String, Object> out = call.execute(in);
        Integer id    = (Integer) out.get("out_id");
        Integer code  = (Integer) out.get("out_status_code");
        String  msg   = (String)  out.get("out_message");
        return new ResultadoCrear(id, nz(code), nz(msg));
    }

    // ----- actualizar -----
    public ResultadoBasico actualizar(int id, PrecioEspecialUpdateRequestDto req) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_precios_especiales_update")
                .declareParameters(
                        new SqlParameter("p_id", Types.INTEGER),
                        new SqlParameter("p_cliente_id", Types.INTEGER),
                        new SqlParameter("p_producto_id", Types.INTEGER),
                        new SqlParameter("p_precio_especial", Types.DECIMAL),
                        new SqlParameter("p_descuento_porcentaje", Types.DECIMAL),
                        new SqlParameter("p_fecha_inicio", Types.DATE),
                        new SqlParameter("p_fecha_vencimiento", Types.DATE),
                        new SqlParameter("p_activo", Types.BIT),

                        new SqlOutParameter("out_status_code", Types.INTEGER),
                        new SqlOutParameter("out_message", Types.NVARCHAR)
                );

        Map<String, Object> in = new HashMap<>();
        in.put("p_id",                   id);
        in.put("p_cliente_id",           req.clienteId());
        in.put("p_producto_id",          req.productoId());
        in.put("p_precio_especial",      toDecimal(req.precioEspecial()));
        in.put("p_descuento_porcentaje", toDecimal(req.descuentoPorcentaje()));
        in.put("p_fecha_inicio",         toDate(req.fechaInicio()));
        in.put("p_fecha_vencimiento",    toDate(req.fechaVencimiento()));
        in.put("p_activo",               bit(req.activo() == null ? Boolean.TRUE : req.activo()));

        Map<String, Object> out = call.execute(in);
        Integer code  = (Integer) out.get("out_status_code");
        String  msg   = (String)  out.get("out_message");
        return new ResultadoBasico(nz(code), nz(msg));
    }

    // ----- activar/inactivar -----
    public ResultadoBasico setActivo(int id, boolean valor) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_precios_especiales_set_activo")
                .declareParameters(
                        new SqlParameter("p_id", Types.INTEGER),
                        new SqlParameter("p_activo", Types.BIT),
                        new SqlOutParameter("out_status_code", Types.INTEGER),
                        new SqlOutParameter("out_message", Types.NVARCHAR)
                );

        Map<String, Object> out = call.execute(Map.of(
                "p_id", id,
                "p_activo", valor ? 1 : 0
        ));
        Integer code  = (Integer) out.get("out_status_code");
        String  msg   = (String)  out.get("out_message");
        return new ResultadoBasico(nz(code), nz(msg));
    }

    // ----- eliminar -----
    public ResultadoBasico eliminar(int id) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_precios_especiales_delete")
                .declareParameters(
                        new SqlParameter("p_id", Types.INTEGER),
                        new SqlOutParameter("out_status_code", Types.INTEGER),
                        new SqlOutParameter("out_message", Types.NVARCHAR)
                );

        Map<String, Object> out = call.execute(Map.of("p_id", id));
        Integer code  = (Integer) out.get("out_status_code");
        String  msg   = (String)  out.get("out_message");
        return new ResultadoBasico(nz(code), nz(msg));
    }

    // ----- resolver precio (SimpleJdbcCall con OUTs) -----
    public ResolverResultado resolver(int clienteId, int productoId, LocalDate fecha) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withProcedureName("sp_precios_especiales_resolver_precio")
                .declareParameters(
                        new SqlParameter("p_cliente_id", Types.INTEGER),
                        new SqlParameter("p_producto_id", Types.INTEGER),
                        new SqlParameter("p_fecha", Types.DATE),
                        new SqlOutParameter("out_precio", Types.DECIMAL),
                        new SqlOutParameter("out_fuente", Types.NVARCHAR),
                        new SqlOutParameter("out_status", Types.INTEGER),
                        new SqlOutParameter("out_message", Types.NVARCHAR)
                );

        Map<String, Object> out = call.execute(new HashMap<>(){{
            put("p_cliente_id", clienteId);
            put("p_producto_id", productoId);
            put("p_fecha", fecha == null ? null : Date.valueOf(fecha));
        }});

        BigDecimal precio = (BigDecimal) out.get("out_precio");
        String fuente     = (String) out.get("out_fuente");
        Integer status    = (Integer) out.get("out_status");
        String message    = (String) out.get("out_message");

        return new ResolverResultado(precio, fuente, nz(status), nz(message));
    }

    // Result wrappers
    public record ResultadoCrear(Integer id, int code, String message) {}
    public record ResultadoBasico(int code, String message) {}
    public record ResolverResultado(BigDecimal precio, String fuente, int code, String message) {}
}
