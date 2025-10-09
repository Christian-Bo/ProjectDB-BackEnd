package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.model.entity.Proveedor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.*;

@Repository
public class ProveedorRepositoryJdbc implements ProveedorRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public ProveedorRepositoryJdbc(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Proveedor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Proveedor p = new Proveedor();
        p.setId(rs.getInt("id"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setNit(rs.getString("nit"));
        p.setTelefono(rs.getString("telefono"));
        p.setDireccion(rs.getString("direccion"));
        p.setEmail(rs.getString("email"));
        p.setDias_credito(rs.getInt("dias_credito"));
        p.setContacto_principal(rs.getString("contacto_principal"));
        p.setActivo(rs.getBoolean("activo"));

        Timestamp ts = rs.getTimestamp("fecha_registro");
        p.setFecha_registro(ts != null ? ts.toInstant().atOffset(ZoneOffset.UTC) : null);

        p.setRegistrado_por(rs.getInt("registrado_por"));
        return p;
    }

    private static MapSqlParameterSource toParams(Proveedor p) {
        return new MapSqlParameterSource()
                .addValue("id", p.getId())
                .addValue("codigo", p.getCodigo())
                .addValue("nombre", p.getNombre())
                .addValue("nit", p.getNit())
                .addValue("telefono", p.getTelefono())
                .addValue("direccion", p.getDireccion())
                .addValue("email", p.getEmail())
                .addValue("dias_credito", p.getDias_credito())
                .addValue("contacto_principal", p.getContacto_principal())
                .addValue("activo", p.getActivo())
                .addValue("registrado_por", p.getRegistrado_por());
    }

    @Override
    public Proveedor save(Proveedor p) throws DataIntegrityViolationException {
        String sql = """
            INSERT INTO proveedores
              (codigo, nombre, nit, telefono, direccion, email, dias_credito, contacto_principal, activo, registrado_por)
            VALUES
              (:codigo, :nombre, :nit, :telefono, :direccion, :email, :dias_credito, :contacto_principal, :activo, :registrado_por)
            """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(sql, toParams(p), kh, new String[]{"id"});
        Number key = kh.getKey();
        p.setId(key != null ? key.intValue() : null);

        return findById(p.getId()).orElse(p);
    }

    @Override
    public Proveedor update(Proveedor p) throws DataIntegrityViolationException {
        String sql = """
            UPDATE proveedores SET
              codigo = :codigo,
              nombre = :nombre,
              nit = :nit,
              telefono = :telefono,
              direccion = :direccion,
              email = :email,
              dias_credito = :dias_credito,
              contacto_principal = :contacto_principal,
              activo = :activo,
              registrado_por = :registrado_por
            WHERE id = :id
            """;
        int n = jdbc.update(sql, toParams(p));
        if (n == 0) return null;
        return findById(p.getId()).orElse(p);
    }

    @Override
    public Optional<Proveedor> findById(Integer id) {
        String sql = "SELECT * FROM proveedores WHERE id = :id";
        List<Proveedor> list = jdbc.query(sql, new MapSqlParameterSource("id", id), ProveedorRepositoryJdbc::mapRow);
        return list.stream().findFirst();
    }

    @Override
    public Optional<Proveedor> findByCodigo(String codigo) {
        String sql = "SELECT * FROM proveedores WHERE codigo = :codigo";
        List<Proveedor> list = jdbc.query(sql, new MapSqlParameterSource("codigo", codigo), ProveedorRepositoryJdbc::mapRow);
        return list.stream().findFirst();
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        String sql = "SELECT COUNT(1) FROM proveedores WHERE codigo = :codigo";
        Integer c = jdbc.queryForObject(sql, new MapSqlParameterSource("codigo", codigo), Integer.class);
        return c != null && c > 0;
    }

    @Override
    public void deleteLogical(Integer id) {
        String sql = "UPDATE proveedores SET activo = 0 WHERE id = :id";
        jdbc.update(sql, new MapSqlParameterSource("id", id));
    }

    @Override
    public List<Proveedor> search(String query, Boolean activo, int page, int size) {
        StringBuilder sb = new StringBuilder("""
            SELECT * FROM proveedores
            WHERE 1=1
            """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (query != null && !query.isBlank()) {
            sb.append("""
                AND (LOWER(nombre) LIKE LOWER(:q)
                  OR LOWER(codigo) LIKE LOWER(:q)
                  OR LOWER(nit) LIKE LOWER(:q))
                """);
            params.addValue("q", "%" + query.trim() + "%");
        }
        if (activo != null) {
            sb.append(" AND activo = :activo ");
            params.addValue("activo", activo);
        }
        sb.append(" ORDER BY nombre ASC OFFSET :off ROWS FETCH NEXT :lim ROWS ONLY ");
        params.addValue("off", Math.max(page, 0) * Math.max(size, 1));
        params.addValue("lim", Math.max(size, 1));

        return jdbc.query(sb.toString(), params, ProveedorRepositoryJdbc::mapRow);
    }

    @Override
    public long countSearch(String query, Boolean activo) {
        StringBuilder sb = new StringBuilder("""
            SELECT COUNT(1) FROM proveedores WHERE 1=1
            """);
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (query != null && !query.isBlank()) {
            sb.append("""
                AND (LOWER(nombre) LIKE LOWER(:q)
                  OR LOWER(codigo) LIKE LOWER(:q)
                  OR LOWER(nit) LIKE LOWER(:q))
                """);
            params.addValue("q", "%" + query.trim() + "%");
        }
        if (activo != null) {
            sb.append(" AND activo = :activo ");
            params.addValue("activo", activo);
        }
        Long total = jdbc.queryForObject(sb.toString(), params, Long.class);
        return total != null ? total : 0L;
    }

    @Override
    public boolean existsEmpleadoById(Integer empleadoId) {
        String sql = "SELECT COUNT(1) FROM empleados WHERE id = :id";
        Integer c = jdbc.queryForObject(sql, new MapSqlParameterSource("id", empleadoId), Integer.class);
        return c != null && c > 0;
    }
}
