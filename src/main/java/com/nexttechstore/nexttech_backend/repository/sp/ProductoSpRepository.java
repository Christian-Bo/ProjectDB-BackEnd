package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductoSpRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ========== Helpers de mapeo (columnas que devuelven tus SPs) ==========
    private Producto mapProducto(ResultSet rs, int rowNum) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));                    // columna real del SP
        p.setSku(rs.getString("codigo"));            // sku <- codigo
        p.setNombre(rs.getString("nombre"));
        // Si tu modelo solo tiene algunos campos, ignora el resto
        p.setCategoriaId(getNullableInt(rs, "categoria_id"));
        p.setMarcaId(getNullableInt(rs, "marca_id"));
        p.setEstado(getNullableInt(rs, "estado"));
        // costo <- precio_compra (ajústalo si quieres precio_venta)
        p.setCosto(getNullableDouble(rs, "precio_compra"));
        return p;
    }

    private Integer getNullableInt(ResultSet rs, String col) throws SQLException {
        Object o = rs.getObject(col);
        return (o == null) ? null : rs.getInt(col);
    }

    private Double getNullableDouble(ResultSet rs, String col) throws SQLException {
        Object o = rs.getObject(col);
        return (o == null) ? null : rs.getDouble(col);
    }

    // ================= LISTAR =================

    public List<Producto> listarProductos() {
        // @UsuarioId obligatorio en el SP; mandamos 0 por simplicidad
        return jdbcTemplate.query(
                "EXEC dbo.sp_productos_listar @UsuarioId = 0",
                this::mapProducto
        );
    }

    public List<Producto> listarProductosConFiltro(String texto, Integer marcaId, Integer categoriaId) {
        return jdbcTemplate.query(
                // Los demás parámetros del SP son opcionales y aceptan NULL
                "EXEC dbo.sp_productos_listar_filtro @UsuarioId = 0, @Texto = ?, @MarcaId = ?, @CategoriaId = ?",
                ps -> {
                    ps.setObject(1, texto);
                    ps.setObject(2, marcaId);
                    ps.setObject(3, categoriaId);
                },
                this::mapProducto
        );
    }

    // ================= GET =================

    public Producto obtenerProductoPorId(int id) {
        List<Producto> list = jdbcTemplate.query(
                "EXEC dbo.sp_productos_get @UsuarioId = 0, @ProductoId = ?",
                ps -> ps.setInt(1, id),
                this::mapProducto
        );
        return list.isEmpty() ? null : list.get(0);
    }

    // ================= CREAR =================
    // Devuelve el nuevo ID (OutDocumentoId) como hace tu AutorRepository
    public Integer crearProducto(Producto p) {
        String sql =
                "DECLARE @OutId INT, @OutMsg NVARCHAR(200); " +
                        "EXEC dbo.sp_productos_crear " +
                        "  @UsuarioId = 0, " +
                        "  @FechaOperacionUTC = NULL, " +
                        "  @codigo = ?, @nombre = ?, @descripcion = ?, " +
                        "  @precio_compra = ?, @precio_venta = ?, " +
                        "  @stock_minimo = ?, @stock_maximo = ?, @estado = ?, " +
                        "  @marca_id = ?, @categoria_id = ?, " +
                        "  @codigo_barras = ?, @unidad_medida = ?, @peso = ?, @garantia_meses = ?, " +
                        "  @OutDocumentoId = @OutId OUTPUT, @OutMensaje = @OutMsg OUTPUT; " +
                        "SELECT @OutId;";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{
                        p.getSku(),
                        p.getNombre(),
                        p.getDescripcion(),                 // puede ser null
                        p.getCosto(),                       // precio_compra
                        p.getPrecioVenta(),                 // null si no lo usas
                        p.getStockMinimo(),                 // null si no lo usas
                        p.getStockMaximo(),                 // null si no lo usas
                        p.getEstado() == null ? 1 : p.getEstado(),
                        p.getMarcaId(),
                        p.getCategoriaId(),
                        p.getCodigoBarras(),
                        p.getUnidadMedida(),
                        p.getPeso(),
                        p.getGarantiaMeses()
                },
                Integer.class
        );
    }

    // ================= ACTUALIZAR =================
    // Devuelve el ID afectado (OutDocumentoId)
    public Integer actualizarProducto(int id, Producto p) {
        String sql =
                "DECLARE @OutId INT, @OutMsg NVARCHAR(200); " +
                        "EXEC dbo.sp_productos_actualizar " +
                        "  @UsuarioId = 0, " +
                        "  @FechaOperacionUTC = NULL, " +
                        "  @ProductoId = ?, " +
                        "  @codigo = ?, @nombre = ?, @descripcion = ?, " +
                        "  @precio_compra = ?, @precio_venta = ?, " +
                        "  @stock_minimo = ?, @stock_maximo = ?, @estado = ?, " +
                        "  @marca_id = ?, @categoria_id = ?, " +
                        "  @codigo_barras = ?, @unidad_medida = ?, @peso = ?, @garantia_meses = ?, " +
                        "  @OutDocumentoId = @OutId OUTPUT, @OutMensaje = @OutMsg OUTPUT; " +
                        "SELECT @OutId;";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{
                        id,
                        p.getSku(),
                        p.getNombre(),
                        p.getDescripcion(),
                        p.getCosto(),
                        p.getPrecioVenta(),
                        p.getStockMinimo(),
                        p.getStockMaximo(),
                        p.getEstado(),
                        p.getMarcaId(),
                        p.getCategoriaId(),
                        p.getCodigoBarras(),
                        p.getUnidadMedida(),
                        p.getPeso(),
                        p.getGarantiaMeses()
                },
                Integer.class
        );
    }

    // ================= CAMBIAR ESTADO / ELIMINAR LÓGICO =================
    // Devuelve el ID afectado (OutDocumentoId)

    public Integer eliminarProductoLogico(int id) {
        return cambiarEstadoProducto(id, 0);
    }

    public Integer activarProducto(int id) {
        return cambiarEstadoProducto(id, 1);
    }

    public Integer cambiarEstadoProducto(int id, int estado) {
        String sql =
                "DECLARE @OutId INT, @OutMsg NVARCHAR(200); " +
                        "EXEC dbo.sp_productos_cambiar_estado " +
                        "  @UsuarioId = 0, " +
                        "  @FechaOperacionUTC = NULL, " +
                        "  @ProductoId = ?, @Estado = ?, " +
                        "  @OutDocumentoId = @OutId OUTPUT, @OutMensaje = @OutMsg OUTPUT; " +
                        "SELECT @OutId;";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{id, estado},
                Integer.class
        );
    }
}
