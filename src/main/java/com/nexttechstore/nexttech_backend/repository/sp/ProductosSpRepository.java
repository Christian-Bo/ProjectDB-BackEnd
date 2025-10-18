package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.catalogos.ProductoDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductosSpRepository {

    private final JdbcTemplate jdbc;

    public ProductosSpRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ================== Mapeo de resultados ==================
    private static final RowMapper<ProductoDto> MAPPER = new RowMapper<>() {
        @Override
        public ProductoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProductoDto p = new ProductoDto();
            p.setId(rs.getInt("id"));
            p.setCodigo(rs.getString("codigo"));
            p.setNombre(rs.getString("nombre"));
            p.setDescripcion(rs.getString("descripcion"));
            p.setPrecioCompra(rs.getDouble("precio_compra"));
            p.setPrecioVenta(rs.getDouble("precio_venta"));
            p.setStockMinimo(rs.getInt("stock_minimo"));
            p.setStockMaximo(rs.getInt("stock_maximo"));
            p.setEstado(rs.getInt("estado"));
            p.setMarcaId(rs.getInt("marca_id"));
            // estos campos son opcionales si los devuelves desde JOIN
            try { p.setMarcaNombre(rs.getString("marca_nombre")); } catch (Exception ignore) {}
            try { p.setCategoriaNombre(rs.getString("categoria_nombre")); } catch (Exception ignore) {}
            p.setCategoriaId(rs.getInt("categoria_id"));
            p.setCodigoBarras(rs.getString("codigo_barras"));
            p.setUnidadMedida(rs.getString("unidad_medida"));
            p.setPeso(rs.getDouble("peso"));
            p.setGarantiaMeses(rs.getInt("garantia_meses"));
            try { p.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime()); } catch (Exception ignore) {}
            p.setCreadoPor(rs.getInt("creado_por"));
            return p;
        }
    };

    // ================== LISTAR ==================
    public List<ProductoDto> listar() {
        return jdbc.query("EXEC sp_PRODUCTOS_Listar", MAPPER);
    }

    // ================== BUSCAR POR ID ==================
    public ProductoDto buscarPorId(int id) {
        return jdbc.queryForObject("EXEC sp_PRODUCTOS_BuscarPorId ?", MAPPER, id);
    }

    // ================== CREAR ==================
    public int crear(ProductoDto p) {
        // ✅ Corregido: solo 14 parámetros (coincide con tu SP)
        String sql = "EXEC sp_PRODUCTOS_Crear ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";

        return jdbc.queryForObject(sql, Integer.class,
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecioCompra(),
                p.getPrecioVenta(),
                p.getStockMinimo(),
                p.getStockMaximo(),
                p.getMarcaId(),
                p.getCategoriaId(),
                p.getCodigoBarras(),
                p.getUnidadMedida(),
                p.getPeso(),
                p.getGarantiaMeses(),
                p.getCreadoPor()
        );
    }

    // ================== EDITAR ==================
    public int editar(int id, ProductoDto p) {
        String sql = "EXEC sp_PRODUCTOS_Editar ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
        return jdbc.queryForObject(sql, Integer.class,
                id,
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecioCompra(),
                p.getPrecioVenta(),
                p.getStockMinimo(),
                p.getStockMaximo(),
                p.getMarcaId(),
                p.getCategoriaId(),
                p.getCodigoBarras(),
                p.getUnidadMedida(),
                p.getPeso(),
                p.getGarantiaMeses()
        );
    }

    // ================== ELIMINAR ==================
    public int eliminar(int id) {
        String sql = "EXEC sp_PRODUCTOS_Eliminar ?";
        return jdbc.queryForObject(sql, Integer.class, id);
    }
}
