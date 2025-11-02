package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class CatalogosQueryRepository {

    private final JdbcTemplate jdbc;

    public CatalogosQueryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---- CLIENTES ----
    public List<ClienteDto> clientes() {
        String sql = """
            SELECT TOP 100 id, codigo, nombre, nit
            FROM dbo.clientes
            ORDER BY id
            """;
        return jdbc.query(sql, (rs, i) -> new ClienteDto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("nit")
        ));
    }

    // ---- BODEGAS ----
    public List<BodegaDto> bodegas() {
        String sql = """
            SELECT id, nombre, direccion
            FROM dbo.bodegas
            ORDER BY id
            """;
        return jdbc.query(sql, (rs, i) -> new BodegaDto(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("direccion")
        ));
    }

    // ---- EMPLEADOS (para Vendedor/Cajero) ----
    public List<EmpleadoDto> empleados() {
        String sql = """
            SELECT id, codigo, nombres, apellidos
            FROM dbo.empleados
            ORDER BY id
            """;
        return jdbc.query(sql, (rs, i) -> new EmpleadoDto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombres"),
                rs.getString("apellidos")
        ));
    }

    // ---- PRODUCTOS CON STOCK (opcional por bodega) ----
    public List<ProductoStockDto> productosConStock(Integer bodegaId) {

        if (bodegaId == null) {
            String sql = """
                SELECT TOP 500
                       p.id,
                       p.codigo,
                       p.nombre,
                       COALESCE(p.precio_venta, 0) AS precio_venta,
                       COALESCE(SUM(i.cantidad_actual), 0) AS stock_disponible
                FROM dbo.productos  AS p
                LEFT JOIN dbo.inventario AS i
                  ON i.producto_id = p.id
                GROUP BY p.id, p.codigo, p.nombre, p.precio_venta
                ORDER BY p.nombre
                """;
            return jdbc.query(sql, (rs, i) -> new ProductoStockDto(
                    rs.getInt("id"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getBigDecimal("precio_venta") != null ? rs.getBigDecimal("precio_venta") : BigDecimal.ZERO,
                    rs.getInt("stock_disponible")
            ));
        }

        String sql = """
            SELECT TOP 500
                   p.id,
                   p.codigo,
                   p.nombre,
                   COALESCE(p.precio_venta, 0) AS precio_venta,
                   COALESCE(i.cantidad_actual, 0) AS stock_disponible
            FROM dbo.productos  AS p
            LEFT JOIN dbo.inventario AS i
              ON i.producto_id = p.id
             AND i.bodega_id   = ?
            ORDER BY p.nombre
            """;

        return jdbc.query(sql, ps -> ps.setInt(1, bodegaId), (rs, i) -> new ProductoStockDto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getBigDecimal("precio_venta") != null ? rs.getBigDecimal("precio_venta") : BigDecimal.ZERO,
                rs.getInt("stock_disponible")
        ));
    }

    // ---- PRODUCTOS LITE (para el modal de Precios Especiales) ----
    public List<ProductoLiteDto> productosLite(String texto, int max) {
        String q = "%" + (texto == null ? "" : texto.trim()) + "%";
        int limit = Math.max(1, Math.min(200, max));
        String sql = """
            SELECT TOP (?) id, codigo, nombre
            FROM dbo.productos
            WHERE estado = 'A'
              AND (codigo LIKE ? OR nombre LIKE ?)
            ORDER BY nombre
        """;
        return jdbc.query(sql, ps -> {
            ps.setInt(1, limit);
            ps.setString(2, q);
            ps.setString(3, q);
        }, (rs, i) -> new ProductoLiteDto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre")
        ));
    }

    // ---- SERIES FACTURA ----
    public List<SerieItem> seriesFactura() {
        String sql = """
            SELECT id, serie, 0 AS correlativo
            FROM dbo.series_facturas
            WHERE tipo_documento = 'F'
            ORDER BY id
            """;
        return jdbc.query(sql, (rs, i) -> new SerieItem(
                rs.getInt("id"),
                rs.getString("serie"),
                rs.getInt("correlativo")
        ));
    }

    public record SerieItem(int id, String serie, int correlativo) {}

    // ================== NUEVO: VENTAS LITE ==================
    public List<VentaLiteDto> ventasLite(int max) {
        String sql = """
            SELECT TOP (?) v.id,
                           v.numero_venta,
                           COALESCE(c.nombre, CONCAT('ID ', v.cliente_id)) AS cliente_nombre
            FROM dbo.ventas v
            LEFT JOIN dbo.clientes c ON c.id = v.cliente_id
            WHERE v.estado <> 'A'
            ORDER BY v.id DESC
        """;
        return jdbc.query(sql, ps -> ps.setInt(1, max), (rs, i) ->
                new VentaLiteDto(
                        rs.getInt("id"),
                        rs.getString("numero_venta"),
                        rs.getString("cliente_nombre")
                )
        );
    }

    // ================== NUEVO: DETALLE LITE POR VENTA ==================
    public List<DetalleVentaLiteDto> detalleVentaLite(int ventaId) {
        String sql = """
            SELECT dv.id,
                   dv.producto_id,
                   COALESCE(p.nombre, CONCAT('ID ', dv.producto_id)) AS producto_nombre,
                   dv.cantidad
            FROM dbo.detalle_ventas dv
            LEFT JOIN dbo.productos p ON p.id = dv.producto_id
            WHERE dv.venta_id = ?
            ORDER BY dv.id
        """;
        return jdbc.query(sql, ps -> ps.setInt(1, ventaId), (rs, i) ->
                new DetalleVentaLiteDto(
                        rs.getInt("id"),
                        rs.getInt("producto_id"),
                        rs.getString("producto_nombre"),
                        rs.getInt("cantidad")
                )
        );
    }
}
