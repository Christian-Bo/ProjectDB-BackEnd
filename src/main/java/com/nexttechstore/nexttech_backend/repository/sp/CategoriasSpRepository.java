package com.nexttechstore.nexttech_backend.repository.sp;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.nexttechstore.nexttech_backend.dto.catalogos.CategoriaDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CategoriasSpRepository {

    private final JdbcTemplate jdbc;

    public CategoriasSpRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<CategoriaDto> MAPPER = new RowMapper<>() {
        @Override
        public CategoriaDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            CategoriaDto c = new CategoriaDto();
            c.setId(rs.getInt("id"));
            c.setNombre(rs.getString("nombre"));
            c.setDescripcion(rs.getString("descripcion"));
            c.setCategoriaPadreId(rs.getObject("categoria_padre_id") != null ? rs.getInt("categoria_padre_id") : null);
            c.setCategoriaPadreNombre(rs.getString("categoria_padre_nombre"));
            c.setActivo(rs.getInt("activo"));
            c.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
            return c;
        }
    };

    public List<CategoriaDto> listar() {
        return jdbc.query("EXEC sp_CATEGORIAS_Listar", MAPPER);
    }

    public CategoriaDto buscarPorId(int id) {
        return jdbc.queryForObject("EXEC sp_CATEGORIAS_BuscarPorId ?", MAPPER, id);
    }

    public int crear(String nombre, String descripcion, Integer categoriaPadreId) {
        String sql = "EXEC sp_CATEGORIAS_Crear ?, ?, ?";
        return jdbc.queryForObject(sql, Integer.class, nombre, descripcion, categoriaPadreId);
    }

    public int editar(int id, String nombre, String descripcion, Integer categoriaPadreId) {
        String sql = "EXEC sp_CATEGORIAS_Editar ?, ?, ?, ?";
        return jdbc.queryForObject(sql, Integer.class, id, nombre, descripcion, categoriaPadreId);
    }

    public int eliminar(int id) {
        String sql = "EXEC sp_CATEGORIAS_Eliminar ?";
        return jdbc.queryForObject(sql, Integer.class, id);
    }
}
