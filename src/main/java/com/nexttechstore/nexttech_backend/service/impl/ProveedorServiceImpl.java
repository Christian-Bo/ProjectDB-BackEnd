package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.ProveedorDto;
import com.nexttechstore.nexttech_backend.service.api.ProveedorService;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.*;

/**
 * Implementación basada en JdbcTemplate (SQL Server).
 *
 * NOTAS TÉCNICAS
 * --------------
 * - Se usa RowMapper para mapear filas -> ProveedorDto.
 * - Paginación: OFFSET / FETCH (SQL Server 2012+).
 * - Insert: se utiliza KeyHolder para recuperar la PK (IDENTITY).
 * - Se asume que la FK (registrado_por) existe en empleados.id.
 * - Eliminación lógica: activo=false.
 * - No se crean nuevos DTOs ni excepciones; se lanzan excepciones estándar
 *   que tus handlers globales ya mapean a 4xx/5xx.
 */
@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final JdbcTemplate jdbc;

    public ProveedorServiceImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ========================
    // RowMapper de Proveedor
    // ========================
    private static final RowMapper<ProveedorDto> PROV_MAPPER = (rs, i) -> {
        ProveedorDto d = new ProveedorDto();
        d.setId(rs.getInt("id"));
        d.setCodigo(rs.getString("codigo"));
        d.setNombre(rs.getString("nombre"));
        d.setNit(rs.getString("nit"));
        d.setTelefono(rs.getString("telefono"));
        d.setDireccion(rs.getString("direccion"));
        d.setEmail(rs.getString("email"));
        d.setDias_credito(getIntegerOrNull(rs, "dias_credito"));
        d.setContacto_principal(rs.getString("contacto_principal"));
        d.setActivo(rs.getBoolean("activo"));
        // fecha_registro existe en la tabla; si tu DTO no lo tiene, se omite
        d.setRegistrado_por(getIntegerOrNull(rs, "registrado_por"));
        return d;
    };

    private static Integer getIntegerOrNull(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    // ==========================================================
    // CREATE
    // ==========================================================
    @Override
    @Transactional
    public ProveedorDto crear(ProveedorDto dto) {
        // Validación mínima: campos requeridos por la BD/negocio
        if (dto.getCodigo() == null || dto.getCodigo().isBlank())
            throw new IllegalArgumentException("El código es obligatorio");
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        if (dto.getNit() == null || dto.getNit().isBlank())
            throw new IllegalArgumentException("El NIT es obligatorio");
        if (dto.getTelefono() == null || dto.getTelefono().isBlank())
            throw new IllegalArgumentException("El teléfono es obligatorio");
        if (dto.getDias_credito() == null || dto.getDias_credito() < 0)
            throw new IllegalArgumentException("Los días de crédito deben ser >= 0");
        if (dto.getRegistrado_por() == null)
            throw new IllegalArgumentException("Debe indicar el empleado que registra");

        // (Opcional) Validar empleado activo:
        validarEmpleadoActivo(dto.getRegistrado_por());

        // Insert con KeyHolder para recuperar ID (IDENTITY)
        final String sql = """
            INSERT INTO proveedores
              (codigo, nombre, nit, telefono, direccion, email, dias_credito,
               contacto_principal, activo, registrado_por)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int idx = 1;
            ps.setString(idx++, dto.getCodigo().trim());
            ps.setString(idx++, dto.getNombre().trim());
            ps.setString(idx++, dto.getNit().trim());
            ps.setString(idx++, dto.getTelefono().trim());
            ps.setString(idx++, nullIfBlank(dto.getDireccion()));
            ps.setString(idx++, nullIfBlank(dto.getEmail()));
            ps.setInt(idx++, dto.getDias_credito());
            ps.setString(idx++, nullIfBlank(dto.getContacto_principal()));
            ps.setBoolean(idx++, dto.getActivo() != null ? dto.getActivo() : true);
            ps.setInt(idx++, dto.getRegistrado_por());
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) {
            // Caso muy raro: si no devolvió PK, consultamos por código
            return obtenerPorCodigo(dto.getCodigo());
        }
        return obtenerPorId(key.intValue());
    }

    // ==========================================================
    // UPDATE
    // ==========================================================
    @Override
    @Transactional
    public ProveedorDto actualizar(Integer id, ProveedorDto dto) {
        ProveedorDto existente = obtenerPorId(id); // lanza excepción si no existe

        // (Opcional) Validar empleado activo si viene en el payload
        if (dto.getRegistrado_por() != null) {
            validarEmpleadoActivo(dto.getRegistrado_por());
        }

        final String sql = """
            UPDATE proveedores SET
              codigo = ?, nombre = ?, nit = ?, telefono = ?, direccion = ?, email = ?,
              dias_credito = ?, contacto_principal = ?, activo = ?, registrado_por = ?
            WHERE id = ?
            """;

        int count = jdbc.update(sql,
                nvl(dto.getCodigo(), existente.getCodigo()).trim(),
                nvl(dto.getNombre(), existente.getNombre()).trim(),
                nvl(dto.getNit(), existente.getNit()).trim(),
                nvl(dto.getTelefono(), existente.getTelefono()).trim(),
                nullIfBlank(nvl(dto.getDireccion(), existente.getDireccion())),
                nullIfBlank(nvl(dto.getEmail(), existente.getEmail())),
                nvl(dto.getDias_credito(), existente.getDias_credito()),
                nullIfBlank(nvl(dto.getContacto_principal(), existente.getContacto_principal())),
                nvl(dto.getActivo(), existente.getActivo()),
                nvl(dto.getRegistrado_por(), existente.getRegistrado_por()),
                id);

        if (count == 0) {
            throw new NoSuchElementException("Proveedor no encontrado para actualizar");
        }
        return obtenerPorId(id);
    }

    // ==========================================================
    // DELETE (lógico)
    // ==========================================================
    @Override
    @Transactional
    public void eliminarLogico(Integer id) {
        final String sql = "UPDATE proveedores SET activo = 0 WHERE id = ?";
        int count = jdbc.update(sql, id);
        if (count == 0) throw new NoSuchElementException("Proveedor no encontrado");
    }

    // ==========================================================
    // READ (por id / por código)
    // ==========================================================
    @Override
    public ProveedorDto obtenerPorId(Integer id) {
        try {
            final String sql = "SELECT * FROM proveedores WHERE id = ?";
            return jdbc.queryForObject(sql, PROV_MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Proveedor no encontrado");
        }
    }

    @Override
    public ProveedorDto obtenerPorCodigo(String codigo) {
        try {
            final String sql = "SELECT * FROM proveedores WHERE codigo = ?";
            return jdbc.queryForObject(sql, PROV_MAPPER, codigo);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Proveedor no encontrado");
        }
    }

    // ==========================================================
    // SEARCH (paginación manual) + COUNT
    // ==========================================================
    @Override
    public List<ProveedorDto> buscar(String q, Boolean activo, int page, int size) {
        StringBuilder sb = new StringBuilder("""
            SELECT *
            FROM proveedores
            WHERE 1=1
            """);

        List<Object> args = new ArrayList<>();
        // Filtro texto (codigo / nombre / nit)
        if (q != null && !q.isBlank()) {
            sb.append(" AND (codigo LIKE ? OR nombre LIKE ? OR nit LIKE ?) ");
            String like = "%" + q.trim() + "%";
            args.add(like); args.add(like); args.add(like);
        }
        // Filtro activo (si viene definido)
        if (activo != null) {
            sb.append(" AND activo = ? ");
            args.add(activo);
        }
        // Orden y paginación
        sb.append(" ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        int offset = Math.max(page, 0) * Math.max(size, 1);
        args.add(offset);
        args.add(Math.max(size, 1));

        return jdbc.query(sb.toString(), PROV_MAPPER, args.toArray());
    }

    @Override
    public long contar(String q, Boolean activo) {
        StringBuilder sb = new StringBuilder("""
            SELECT COUNT(*) FROM proveedores WHERE 1=1
            """);
        List<Object> args = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            sb.append(" AND (codigo LIKE ? OR nombre LIKE ? OR nit LIKE ?) ");
            String like = "%" + q.trim() + "%";
            args.add(like); args.add(like); args.add(like);
        }
        if (activo != null) {
            sb.append(" AND activo = ? ");
            args.add(activo);
        }
        return jdbc.queryForObject(sb.toString(), Long.class, args.toArray());
    }

    // ==========================================================
    // EMPLEADOS (para combo)  GET /api/proveedores/_empleados
    // ==========================================================
    @Override
    public List<Map<String, Object>> listarEmpleadosActivosMin() {
        final String sql = """
            SELECT id,
                   (nombres + ' ' + apellidos) AS nombre
            FROM empleados
            WHERE estado = 'A'
            ORDER BY nombres, apellidos
            """;
        return jdbc.query(sql, (rs, i) -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", rs.getInt("id"));
            m.put("nombre", rs.getString("nombre"));
            return m;
        });
    }

    // ==========================================================
    // Helpers
    // ==========================================================
    private static String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static <T> T nvl(T v, T def) {
        return v != null ? v : def;
    }

    /** Verifica que el empleado exista y esté activo (estado='A'). */
    private void validarEmpleadoActivo(Integer empleadoId) throws DataAccessException {
        final String sql = "SELECT COUNT(*) FROM empleados WHERE id = ? AND estado = 'A'";
        Integer count = jdbc.queryForObject(sql, Integer.class, empleadoId);
        if (count == null || count == 0) {
            throw new NoSuchElementException("El empleado indicado no existe o no está activo");
        }
    }
}
