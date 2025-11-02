package com.nexttechstore.nexttech_backend.repository.orm;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class PreciosEspecialesQueryRepository {

    private final JdbcTemplate jdbc;

    public PreciosEspecialesQueryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> listar(String texto, Boolean activo,
                                            LocalDate desde, LocalDate hasta,
                                            int page, int size) {
        StringBuilder sql = new StringBuilder(
                "EXEC dbo.sp_precios_especiales_list @p_texto=?, @p_activo=?, @p_desde=?, @p_hasta=?, @p_page=?, @p_size=?"
        );
        List<Object> args = new ArrayList<>();
        args.add((texto == null || texto.isBlank()) ? null : texto.trim());
        args.add(activo == null ? null : (activo ? 1 : 0));
        args.add(desde == null ? null : Date.valueOf(desde));
        args.add(hasta == null ? null : Date.valueOf(hasta));
        args.add(Math.max(0, page));
        args.add(Math.max(1, size));

        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public Map<String, Object> getById(int id) {
        String sql = "EXEC dbo.sp_precios_especiales_get_by_id @p_id=?";
        List<Map<String, Object>> list = jdbc.queryForList(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Map<String, Object> resolverPrecio(int clienteId, int productoId, LocalDate fecha) {
        String sql = "EXEC dbo.sp_precios_especiales_resolver_precio " +
                "@p_cliente_id=?, @p_producto_id=?, @p_fecha=?, " +
                "@out_precio=?, @out_fuente=?, @out_status=?, @out_message=?";
        // Nota: Para SP con OUTs, es más confiable usar SimpleJdbcCall.
        // Aquí devolvemos a través de SimpleJdbcCall en el CommandRepository (método dedicado).
        // Dejamos este método sin uso para evitar comportamientos inconsistentes.
        return Map.of(); // No usar. Resolver con CommandRepository.simpleResolver(...)
    }
}
