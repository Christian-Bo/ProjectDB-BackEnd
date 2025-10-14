package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.dto.catalogos.BodegaDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ClienteDto;
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

    // ClienteDto: (Integer id, String codigo, String nombre, String nit)
    public List<ClienteDto> clientes() {
        // NOTA: no filtramos por 'estado' porque en tu BD es VARCHAR ('A', ...) y/o puede no existir.
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

    // BodegaDto: (Integer id, String nombre, String direccion)
    public List<BodegaDto> bodegas() {
        // NOTA: tu tabla no tiene 'estado', así que no filtramos por esa columna.
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

    // ProductoStockDto: (Integer id, String codigo, String nombre, BigDecimal precioVenta, Integer stockDisponible)
    public List<ProductoStockDto> productosConStock() {
        // NOTA: evitamos filtrar por 'estado' en productos por si es VARCHAR o no existe.
        //       Asumimos 'inventario.cantidad_actual' existe (lo usaste en otros SP).
        String sql = """
            SELECT TOP 200
                   p.id,
                   p.codigo,
                   p.nombre,
                   COALESCE(p.precio_venta, 0) AS precio_venta,
                   i.cantidad_actual AS stock_disponible
            FROM dbo.productos p
            JOIN dbo.inventario i ON i.producto_id = p.id
            ORDER BY p.id
            """;
        return jdbc.query(sql, (rs, i) -> new ProductoStockDto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getBigDecimal("precio_venta") != null ? rs.getBigDecimal("precio_venta") : BigDecimal.ZERO,
                rs.getInt("stock_disponible")
        ));
    }

    // Series para facturas (id, serie, correlativo "visible")
    public List<SerieItem> seriesFactura() {
        /*
         * Tu tabla no tiene 'correlativo' (exacto) o su nombre difiere.
         * Para no fallar y aun así mostrar algo útil al front (id y serie),
         * devolvemos 'correlativo' como 0 (constante). Si luego confirmas el
         * nombre real (p.ej. 'numero_actual', 'correlativo_actual', etc.),
         * reemplazamos ese 0 por la columna correcta.
         */
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
