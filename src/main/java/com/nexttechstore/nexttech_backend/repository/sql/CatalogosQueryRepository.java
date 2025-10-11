package com.nexttechstore.nexttech_backend.repository.sql;

import com.nexttechstore.nexttech_backend.dto.catalogos.BodegaDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ClienteDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.EmpleadoDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ProductoStockDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CatalogosQueryRepository {

    private final JdbcTemplate jdbc;

    public CatalogosQueryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /* ================== CLIENTES ================== */
    public List<ClienteDto> buscarClientes(String q, int limit) {
        String base = """
            SELECT TOP (?) c.id, c.codigo, c.nombre, ISNULL(c.nit,'') AS nit
            FROM dbo.clientes c
            WHERE c.estado='A'
              AND (
                    ? IS NULL
                 OR c.codigo LIKE '%' + ? + '%'
                 OR c.nombre LIKE '%' + ? + '%'
                 OR c.nit    LIKE '%' + ? + '%'
              )
            ORDER BY c.nombre
            """;
        return jdbc.query(base,
                (rs, i) -> new ClienteDto(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("nit")
                ),
                limit,
                q, q, q, q
        );
    }

    /* ================== EMPLEADOS ================== */
    public List<EmpleadoDto> buscarEmpleados(String q, int limit) {
        String sql = """
            SELECT TOP (?) e.id, e.codigo, e.nombres, e.apellidos
            FROM dbo.empleados e
            WHERE e.estado='A'
              AND ( ? IS NULL
                 OR e.codigo   LIKE '%' + ? + '%'
                 OR e.nombres  LIKE '%' + ? + '%'
                 OR e.apellidos LIKE '%' + ? + '%'
              )
            ORDER BY e.nombres, e.apellidos
            """;
        return jdbc.query(sql,
                (rs, i) -> new EmpleadoDto(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nombres"),
                        rs.getString("apellidos")
                ),
                limit,
                q, q, q, q
        );
    }

    /* ================== BODEGAS ================== */
    public List<BodegaDto> buscarBodegas(String q, int limit) {
        String sql = """
            SELECT TOP (?) b.id, b.nombre, ISNULL(b.direccion,'') AS direccion
            FROM dbo.bodegas b
            WHERE b.activo = 1
              AND ( ? IS NULL
                 OR b.nombre    LIKE '%' + ? + '%'
                 OR b.direccion LIKE '%' + ? + '%'
              )
            ORDER BY b.nombre
            """;
        return jdbc.query(sql,
                (rs, i) -> new BodegaDto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("direccion")
                ),
                limit,
                q, q, q
        );
    }

    /* ============= PRODUCTOS + STOCK EN BODEGA ============= */
    public List<ProductoStockDto> buscarProductosConStock(Integer bodegaId, String q, int limit) {
        String sql = """
            SELECT TOP (?) p.id, p.codigo, p.nombre,
                   ISNULL(p.precio_venta, 0) AS precio_venta,
                   ISNULL(i.cantidad_actual, 0) AS cantidad_actual
            FROM dbo.productos p
            LEFT JOIN dbo.inventario i
              ON i.producto_id = p.id
             AND i.bodega_id   = ?
            WHERE p.estado = 'A'
              AND ( ? IS NULL
                 OR p.codigo LIKE '%' + ? + '%'
                 OR p.nombre LIKE '%' + ? + '%'
              )
            ORDER BY p.nombre
            """;
        return jdbc.query(sql,
                (rs, i) -> new ProductoStockDto(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio_venta"),
                        rs.getInt("cantidad_actual")
                ),
                limit, bodegaId,
                q, q, q
        );
    }

    public Optional<ProductoStockDto> obtenerProductoConStock(Integer bodegaId, Integer productoId) {
        String sql = """
            SELECT p.id, p.codigo, p.nombre,
                   ISNULL(p.precio_venta, 0) AS precio_venta,
                   ISNULL(i.cantidad_actual, 0) AS cantidad_actual
            FROM dbo.productos p
            LEFT JOIN dbo.inventario i
              ON i.producto_id = p.id
             AND i.bodega_id   = ?
            WHERE p.id = ?
            """;
        List<ProductoStockDto> list = jdbc.query(sql,
                (rs, i) -> new ProductoStockDto(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio_venta"),
                        rs.getInt("cantidad_actual")
                ),
                bodegaId, productoId
        );
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
