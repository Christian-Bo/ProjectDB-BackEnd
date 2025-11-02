package com.nexttechstore.nexttech_backend.repository.orm;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ClientesQueryRepository {

    private final JdbcTemplate jdbc;

    public ClientesQueryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Listado con filtros y paginación */
    public List<Map<String, Object>> listar(String texto, String estado, String tipo, int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 50;
        // Uso EXEC directo al SP de listado (sin OUT params)
        String sql = "EXEC dbo.sp_clientes_list ?, ?, ?, ?, ?";
        return jdbc.queryForList(sql, texto, estado, tipo, page, size);
    }

    /** Obtener detalle por id */
    public Map<String, Object> getById(int id) {
        String sql = "EXEC dbo.sp_clientes_get ?";
        return jdbc.queryForMap(sql, id);
    }

    /** Lite para combos/autocomplete */
    public List<Map<String, Object>> lite(String texto, int max) {
        String sql = "EXEC dbo.sp_clientes_lite ?, ?";
        return jdbc.queryForList(sql, texto, max);
    }

    /** Siguiente código CLI-### */
    public String nextCodigo() {
        String sql = "EXEC dbo.sp_clientes_next_codigo";
        Map<String, Object> m = jdbc.queryForMap(sql);
        Object v = m.get("next_codigo");
        return v == null ? null : String.valueOf(v);
    }
}
