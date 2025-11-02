// src/main/java/com/nexttechstore/nexttech_backend/repository/orm/DevolucionesQueryRepository.java
package com.nexttechstore.nexttech_backend.repository.orm;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DevolucionesQueryRepository {

    private final JdbcTemplate jdbc;

    public DevolucionesQueryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Listado de devoluciones con datos listos para la UI:
     *  - numero (alias de numero_devolucion)
     *  - fecha  (yyyy-MM-dd)
     *  - venta_id, numero_venta
     *  - cliente_nombre
     *  - estado
     */
    public List<Map<String, Object>> listar(LocalDate desde, LocalDate hasta,
                                            Integer clienteId, String numero,
                                            int page, int size) {

        StringBuilder sql = new StringBuilder(
                "SELECT d.id, " +
                        "       d.numero_devolucion      AS numero, " +
                        "       CONVERT(varchar(10), d.fecha_devolucion, 23) AS fecha, " +
                        "       d.estado, " +
                        "       d.venta_id, " +
                        "       v.numero_venta            AS numero_venta, " +
                        "       c.nombre                  AS cliente_nombre " +
                        "FROM dbo.devoluciones_venta d " +
                        "LEFT JOIN dbo.ventas   v ON v.id = d.venta_id " +
                        "LEFT JOIN dbo.clientes c ON c.id = v.cliente_id " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (desde != null) {
            sql.append(" AND d.fecha_devolucion >= ? ");
            params.add(Date.valueOf(desde));
        }
        if (hasta != null) {
            sql.append(" AND d.fecha_devolucion <= ? ");
            params.add(Date.valueOf(hasta));
        }
        if (clienteId != null) {
            sql.append(" AND v.cliente_id = ? ");
            params.add(clienteId);
        }
        if (numero != null && !numero.isBlank()) {
            sql.append(" AND d.numero_devolucion = ? ");
            params.add(numero.trim());
        }

        sql.append(" ORDER BY d.id DESC ");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");

        int offset = Math.max(page, 0) * Math.max(size, 1);
        params.add(offset);
        params.add(size);

        return jdbc.queryForList(sql.toString(), params.toArray());
    }

    /**
     * Saldos por venta (para validar máximos en el modal).
     */
    public List<Map<String, Object>> saldosPorVenta(int ventaId) {
        String sql = """
            SELECT dv.id              AS detalle_venta_id,
                   dv.producto_id     AS producto_id,
                   dv.cantidad        AS vendido,
                   COALESCE(SUM(CASE WHEN d.estado = 'A' THEN ddv.cantidad ELSE 0 END), 0) AS devuelto,
                   dv.cantidad - COALESCE(SUM(CASE WHEN d.estado = 'A' THEN ddv.cantidad ELSE 0 END), 0) AS saldo
            FROM dbo.detalle_ventas dv
            LEFT JOIN dbo.detalle_devoluciones_venta ddv
              ON ddv.detalle_venta_id = dv.id
            LEFT JOIN dbo.devoluciones_venta d
              ON d.id = ddv.devolucion_id
            WHERE dv.venta_id = ?
            GROUP BY dv.id, dv.producto_id, dv.cantidad
            ORDER BY dv.id
        """;
        return jdbc.queryForList(sql, ventaId);
    }

    // ===== NUEVOS para el detalle =====

    /** Encabezado de la devolución */
    public Map<String, Object> obtenerHeader(int devolucionId) {
        String sql =
                "SELECT d.id, " +
                        "       d.numero_devolucion                    AS numero, " +
                        "       CONVERT(varchar(10), d.fecha_devolucion, 23) AS fecha, " +
                        "       d.estado, " +
                        "       d.motivo, " +
                        "       d.venta_id, " +
                        "       v.numero_venta                          AS numero_venta, " +
                        "       v.bodega_origen_id                      AS bodega_id, " +
                        "       c.nombre                                AS cliente_nombre " +
                        "FROM dbo.devoluciones_venta d " +
                        "LEFT JOIN dbo.ventas   v ON v.id = d.venta_id " +
                        "LEFT JOIN dbo.clientes c ON c.id = v.cliente_id " +
                        "WHERE d.id = ?";

        try {
            return jdbc.queryForMap(sql, devolucionId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    /** Ítems de la devolución */
    public List<Map<String, Object>> obtenerItems(int devolucionId) {
        String sql =
                "SELECT dd.id                 AS detalle_id, " +
                        "       dd.detalle_venta_id, " +
                        "       dd.producto_id, " +
                        "       p.nombre              AS producto_nombre, " +
                        "       dv.cantidad           AS vendido, " +
                        "       dd.cantidad           AS devuelto, " +
                        "       dd.observaciones      AS observaciones " +
                        "FROM dbo.detalle_devoluciones_venta dd " +
                        "LEFT JOIN dbo.productos p     ON p.id = dd.producto_id " +
                        "LEFT JOIN dbo.detalle_ventas dv ON dv.id = dd.detalle_venta_id " +
                        "WHERE dd.devolucion_id = ? " +
                        "ORDER BY dd.id ASC";
        return jdbc.queryForList(sql, devolucionId);
    }
}
