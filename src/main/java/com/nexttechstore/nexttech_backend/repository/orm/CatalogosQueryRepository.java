package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.dto.catalogos.BodegaDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ClienteDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.EmpleadoDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ProductoStockDto;
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
        String base = """
            SELECT TOP 500
                   p.id,
                   p.codigo,
                   p.nombre,
                   COALESCE(p.precio_venta, 0) AS precio_venta,
                   i.cantidad_actual          AS stock_disponible
            FROM dbo.productos  AS p
            JOIN dbo.inventario AS i ON i.producto_id = p.id
            """;
        String sql = (bodegaId == null)
                ? base + " ORDER BY p.id"
                : base + " WHERE i.bodega_id = ? ORDER BY p.id";

        return (bodegaId == null)
                ? jdbc.query(sql, (rs, i) -> new ProductoStockDto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getBigDecimal("precio_venta") != null ? rs.getBigDecimal("precio_venta") : BigDecimal.ZERO,
                rs.getInt("stock_disponible")
        ))
                : jdbc.query(sql, ps -> ps.setInt(1, bodegaId), (rs, i) -> new ProductoStockDto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getBigDecimal("precio_venta") != null ? rs.getBigDecimal("precio_venta") : BigDecimal.ZERO,
                rs.getInt("stock_disponible")
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
}
