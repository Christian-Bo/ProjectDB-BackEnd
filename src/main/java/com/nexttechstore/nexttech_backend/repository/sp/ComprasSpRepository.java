package com.nexttechstore.nexttech_backend.repository.sp;

import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.nexttechstore.nexttech_backend.exception.BadRequestException;
import com.nexttechstore.nexttech_backend.exception.ResourceNotFoundException;
import com.nexttechstore.nexttech_backend.model.compras.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

// ====== IMPORTS JDBC EXPLÍCITOS ======
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

// ====== FECHAS Y COLECCIONES ======
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;           // firma pública
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Repositorio de acceso a SPs de Compras + catálogos para combos.
 *
 * SPs utilizados:
 *  - dbo.sp_COMPRAS_Listar
 *  - dbo.sp_COMPRAS_ObtenerPorId           (2 RS: cabecera con nombres + detalle con nombres)
 *  - dbo.sp_COMPRAS_Crear                  (TVP dbo.tvp_DetalleCompra + OUT @CompraIdOut + RS cabecera/detalle)
 *  - dbo.sp_COMPRAS_EditarCabecera         (RS cabecera con nombres)
 *  - dbo.sp_COMPRAS_AgregarDetalle         (TVP + RS líneas insertadas con nombres)
 *  - dbo.sp_COMPRAS_EditarDetalleLinea     (RS línea actualizada con nombres)
 *  - dbo.sp_COMPRAS_QuitarDetalleLinea
 *  - dbo.sp_COMPRAS_Anular
 *  - dbo.sp_PRODUCTOS_AutoFill             (autollenado por producto + stock por bodega opcional)
 *  - dbo.sp_COMPRAS_Lookups                (3 RS: proveedores, bodegas, empleados)
 */
@Repository
public class ComprasSpRepository {

    private static final Logger log = LoggerFactory.getLogger(ComprasSpRepository.class);

    private final JdbcTemplate jdbc;

    public ComprasSpRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ========= Helpers de conversión seguros =========

    private Integer getInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    private BigDecimal getBig(ResultSet rs, String col) throws SQLException {
        return rs.getBigDecimal(col);
    }

    private LocalDate getDate(ResultSet rs, String col) throws SQLException {
        java.sql.Date d = rs.getDate(col);
        return (d == null) ? null : d.toLocalDate();
    }

    private OffsetDateTime getOffsetDateTime(ResultSet rs, String col) throws SQLException {
        Timestamp ts = rs.getTimestamp(col);
        return (ts == null) ? null : ts.toInstant().atOffset(ZoneOffset.UTC);
    }

    private boolean hasColumn(ResultSet rs, String name) {
        try {
            ResultSetMetaData md = rs.getMetaData();
            int c = md.getColumnCount();
            for (int i = 1; i <= c; i++) {
                if (name.equalsIgnoreCase(md.getColumnLabel(i)) || name.equalsIgnoreCase(md.getColumnName(i))) {
                    return true;
                }
            }
        } catch (SQLException ignored) {}
        return false;
    }

    // ========= LISTAR =========
    // Ahora el SP devuelve proveedor_nombre y bodega_destino_nombre, los mapeamos directo.
    public List<CompraListItem> listar(Date fechaDel, Date fechaAl, Integer proveedorId, String estado, String texto) {
        return jdbc.query(
                "EXEC dbo.sp_COMPRAS_Listar @FechaDel=?, @FechaAl=?, @ProveedorId=?, @Estado=?, @Texto=?",
                ps -> {
                    if (fechaDel != null) ps.setDate(1, new java.sql.Date(fechaDel.getTime())); else ps.setNull(1, Types.DATE);
                    if (fechaAl  != null) ps.setDate(2, new java.sql.Date(fechaAl .getTime())); else ps.setNull(2, Types.DATE);
                    if (proveedorId != null) ps.setInt(3, proveedorId); else ps.setNull(3, Types.INTEGER);
                    if (estado != null && !estado.isBlank()) ps.setString(4, estado); else ps.setNull(4, Types.CHAR);
                    if (texto != null && !texto.isBlank()) ps.setString(5, texto); else ps.setNull(5, Types.NVARCHAR);
                },
                (rs, row) -> {
                    CompraListItem it = new CompraListItem();
                    it.setId(getInt(rs, "id"));
                    it.setNumeroCompra(rs.getString("numero_compra"));
                    it.setNoFacturaProveedor(rs.getString("no_factura_proveedor"));
                    it.setFechaCompra(getDate(rs, "fecha_compra"));
                    it.setSubtotal(getBig(rs, "subtotal"));
                    it.setDescuentoGeneral(getBig(rs, "descuento_general"));
                    it.setIva(getBig(rs, "iva"));
                    it.setTotal(getBig(rs, "total"));
                    it.setEstado(rs.getString("estado"));
                    it.setProveedorId(getInt(rs, "proveedor_id"));
                    it.setBodegaDestinoId(getInt(rs, "bodega_destino_id"));
                    it.setFechaCreacion(getOffsetDateTime(rs, "fecha_creacion"));
                    // Nuevos campos devueltos por el SP
                    if (hasColumn(rs, "proveedor_nombre")) {
                        it.setProveedorNombre(rs.getString("proveedor_nombre"));
                    }
                    if (hasColumn(rs, "bodega_destino_nombre")) {
                        it.setBodegaDestinoNombre(rs.getString("bodega_destino_nombre"));
                    }
                    return it;
                }
        );
    }

    // ========= OBTENER POR ID =========
    // El SP ya devuelve nombres en cabecera y detalle.
    public CompraFull obtenerPorId(int compraId) {
        return jdbc.execute((Connection con) -> {
            try {
                String call = "{call dbo.sp_COMPRAS_ObtenerPorId(?)}";
                try (CallableStatement cs = con.prepareCall(call)) {
                    cs.setInt(1, compraId);
                    boolean hasResults = cs.execute();
                    CompraCabecera cabecera = null;
                    List<CompraDetalle> detalle = new ArrayList<>();

                    int rsIndex = 0;
                    while (hasResults) {
                        try (ResultSet rs = cs.getResultSet()) {
                            if (rs != null) {
                                if (rsIndex == 0) {
                                    if (rs.next()) cabecera = mapCabeceraConNombres(rs);
                                } else if (rsIndex == 1) {
                                    while (rs.next()) detalle.add(mapDetalleConNombres(rs));
                                }
                            }
                        }
                        hasResults = cs.getMoreResults();
                        rsIndex++;
                    }

                    if (cabecera == null) {
                        throw new ResourceNotFoundException("Compra no encontrada (id=" + compraId + ")");
                    }
                    return new CompraFull(cabecera, detalle);
                }
            } catch (SQLException ex) {
                throw new BadRequestException("Error al obtener compra: " + ex.getMessage());
            }
        });
    }

    private CompraCabecera mapCabeceraConNombres(ResultSet rs) throws SQLException {
        CompraCabecera c = new CompraCabecera();
        c.setId(getInt(rs, "id"));
        c.setNumeroCompra(rs.getString("numero_compra"));
        c.setNoFacturaProveedor(rs.getString("no_factura_proveedor"));
        c.setFechaCompra(getDate(rs, "fecha_compra"));
        c.setSubtotal(getBig(rs, "subtotal"));
        c.setDescuentoGeneral(getBig(rs, "descuento_general"));
        c.setIva(getBig(rs, "iva"));
        c.setTotal(getBig(rs, "total"));
        c.setEstado(rs.getString("estado"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setProveedorId(getInt(rs, "proveedor_id"));
        c.setEmpleadoCompradorId(getInt(rs, "empleado_comprador_id"));
        c.setEmpleadoAutorizaId(getInt(rs, "empleado_autoriza_id"));
        c.setBodegaDestinoId(getInt(rs, "bodega_destino_id"));
        c.setFechaCreacion(getOffsetDateTime(rs, "fecha_creacion"));

        // Nombres amigables (siempre devueltos por el SP; fallback defensivo)
        if (hasColumn(rs, "proveedor_nombre")) c.setProveedorNombre(rs.getString("proveedor_nombre"));
        if (hasColumn(rs, "empleado_comprador_nombre")) c.setEmpleadoCompradorNombre(rs.getString("empleado_comprador_nombre"));
        if (hasColumn(rs, "empleado_autoriza_nombre")) c.setEmpleadoAutorizaNombre(rs.getString("empleado_autoriza_nombre"));
        if (hasColumn(rs, "bodega_destino_nombre")) c.setBodegaDestinoNombre(rs.getString("bodega_destino_nombre"));
        return c;
    }

    private CompraDetalle mapDetalleConNombres(ResultSet rs) throws SQLException {
        CompraDetalle d = new CompraDetalle();
        d.setId(getInt(rs, "id"));
        d.setCompraId(getInt(rs, "compra_id"));
        d.setProductoId(getInt(rs, "producto_id"));
        d.setCantidadPedida(getInt(rs, "cantidad_pedida"));
        d.setCantidadRecibida(getInt(rs, "cantidad_recibida"));
        d.setPrecioUnitario(getBig(rs, "precio_unitario"));
        d.setDescuentoLinea(getBig(rs, "descuento_linea"));
        d.setSubtotal(getBig(rs, "subtotal"));
        d.setLote(rs.getString("lote"));
        d.setFechaVencimiento(getDate(rs, "fecha_vencimiento"));

        if (hasColumn(rs, "producto_nombre")) d.setProductoNombre(rs.getString("producto_nombre"));
        if (hasColumn(rs, "producto_codigo")) d.setProductoCodigo(rs.getString("producto_codigo"));
        if (hasColumn(rs, "unidad_medida")) d.setUnidadMedida(rs.getString("unidad_medida"));
        return d;
    }

    // ========= CREAR (TVP + OUT) =========
    // Conservamos devolver el ID (compatibilidad). El SP además entrega RS con nombres (el front
    // podría llamarlo luego con obtenerPorId si necesita la representación completa).
    public int crear(CompraCrearRequest req) {
        return jdbc.execute((Connection con) -> {
            try {
                SQLServerConnection sqlCon = con.unwrap(SQLServerConnection.class);
                String call = "{call dbo.sp_COMPRAS_Crear(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                try (SQLServerCallableStatement cs = (SQLServerCallableStatement) sqlCon.prepareCall(call)) {

                    cs.setInt(1,  req.getUsuarioId());
                    cs.setString(2, req.getNumeroCompra());
                    cs.setString(3, req.getNoFacturaProveedor());
                    cs.setDate(4,  java.sql.Date.valueOf(req.getFechaCompra()));
                    cs.setInt(5,  req.getProveedorId());
                    cs.setInt(6,  req.getEmpleadoCompradorId());
                    if (req.getEmpleadoAutorizaId() != null) cs.setInt(7, req.getEmpleadoAutorizaId()); else cs.setNull(7, Types.INTEGER);
                    cs.setInt(8,  req.getBodegaDestinoId());
                    if (req.getObservaciones() != null && !req.getObservaciones().isBlank())
                        cs.setString(9, req.getObservaciones());
                    else
                        cs.setNull(9, Types.NVARCHAR);

                    // TVP @Detalle
                    SQLServerDataTable tvp = new SQLServerDataTable();
                    tvp.addColumnMetadata("producto_id",       Types.INTEGER);
                    tvp.addColumnMetadata("cantidad_pedida",   Types.INTEGER);
                    tvp.addColumnMetadata("precio_unitario",   Types.DECIMAL);
                    tvp.addColumnMetadata("descuento_linea",   Types.DECIMAL);
                    tvp.addColumnMetadata("lote",              Types.NVARCHAR);
                    tvp.addColumnMetadata("fecha_vencimiento", Types.DATE);

                    for (CompraDetalleRequest li : req.getDetalle()) {
                        tvp.addRow(
                                li.getProductoId(),
                                li.getCantidadPedida(),
                                li.getPrecioUnitario(),
                                (li.getDescuentoLinea() != null ? li.getDescuentoLinea() : BigDecimal.ZERO),
                                li.getLote(),
                                (li.getFechaVencimiento() != null ? java.sql.Date.valueOf(li.getFechaVencimiento()) : null)
                        );
                    }

                    cs.setStructured(10, "dbo.tvp_DetalleCompra", tvp);

                    cs.registerOutParameter(11, Types.INTEGER); // @CompraIdOut
                    cs.execute(); // puede devolver RS; los ignoramos aquí

                    return cs.getInt(11);
                }
            } catch (SQLException ex) {
                throw new BadRequestException("Error al crear compra: " + ex.getMessage());
            }
        });
    }

    // ========= EDITAR CABECERA =========
    // Importante: como el SP devuelve un SELECT, usamos CallableStatement#execute() y no update().
    public int editarCabecera(CompraEditarCabeceraRequest req) {
        return jdbc.execute((Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{call dbo.sp_COMPRAS_EditarCabecera(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
                cs.setInt(1, req.getUsuarioId());
                cs.setInt(2, req.getCompraId());
                cs.setString(3, req.getNoFacturaProveedor());
                cs.setDate(4, java.sql.Date.valueOf(req.getFechaCompra()));
                cs.setInt(5, req.getProveedorId());
                cs.setInt(6, req.getEmpleadoCompradorId());
                if (req.getEmpleadoAutorizaId() != null) cs.setInt(7, req.getEmpleadoAutorizaId()); else cs.setNull(7, Types.INTEGER);
                cs.setInt(8, req.getBodegaDestinoId());
                if (req.getDescuentoGeneral() != null) cs.setBigDecimal(9, BigDecimal.valueOf(req.getDescuentoGeneral())); else cs.setNull(9, Types.DECIMAL);
                if (req.getObservaciones() != null && !req.getObservaciones().isBlank()) cs.setString(10, req.getObservaciones()); else cs.setNull(10, Types.NVARCHAR);

                cs.execute(); // consume RS (no necesitamos mapear aquí)
                // Retornamos 1 para “ok” (podrías contar updates si quieres ser más estricto)
                return 1;
            } catch (SQLException ex) {
                throw new BadRequestException("Error al editar cabecera: " + ex.getMessage());
            }
        });
    }

    // ========= AGREGAR DETALLE (1..n) =========
    // El SP devuelve las líneas insertadas con nombres → las consumimos y devolvemos el conteo insertado.
    public int agregarDetalle(int usuarioId, int compraId, List<CompraDetalleRequest> lineas) {
        return jdbc.execute((Connection con) -> {
            try {
                SQLServerConnection sqlCon = con.unwrap(SQLServerConnection.class);
                String call = "{call dbo.sp_COMPRAS_AgregarDetalle(?, ?, ?)}";
                try (SQLServerCallableStatement cs = (SQLServerCallableStatement) sqlCon.prepareCall(call)) {
                    cs.setInt(1, usuarioId);
                    cs.setInt(2, compraId);

                    SQLServerDataTable tvp = new SQLServerDataTable();
                    tvp.addColumnMetadata("producto_id",       Types.INTEGER);
                    tvp.addColumnMetadata("cantidad_pedida",   Types.INTEGER);
                    tvp.addColumnMetadata("precio_unitario",   Types.DECIMAL);
                    tvp.addColumnMetadata("descuento_linea",   Types.DECIMAL);
                    tvp.addColumnMetadata("lote",              Types.NVARCHAR);
                    tvp.addColumnMetadata("fecha_vencimiento", Types.DATE);

                    for (CompraDetalleRequest li : lineas) {
                        tvp.addRow(
                                li.getProductoId(),
                                li.getCantidadPedida(),
                                li.getPrecioUnitario(),
                                (li.getDescuentoLinea() != null ? li.getDescuentoLinea() : BigDecimal.ZERO),
                                li.getLote(),
                                (li.getFechaVencimiento() != null ? java.sql.Date.valueOf(li.getFechaVencimiento()) : null)
                        );
                    }

                    cs.setStructured(3, "dbo.tvp_DetalleCompra", tvp);

                    int inserted = 0;
                    boolean hasRs = cs.execute();
                    while (hasRs) {
                        try (ResultSet rs = cs.getResultSet()) {
                            if (rs != null) {
                                // contamos filas devueltas (últimas insertadas)
                                while (rs.next()) inserted++;
                            }
                        }
                        hasRs = cs.getMoreResults();
                    }
                    return inserted;
                }
            } catch (SQLException ex) {
                throw new BadRequestException("Error al agregar detalle: " + ex.getMessage());
            }
        });
    }

    // ========= EDITAR DETALLE (1 línea) =========
    // El SP devuelve la línea resultante; la consumimos y devolvemos 1 (ok).
    public int editarDetalle(CompraEditarDetalleRequest req) {
        return jdbc.execute((Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{call dbo.sp_COMPRAS_EditarDetalleLinea(?, ?, ?, ?, ?, ?, ?)}")) {
                cs.setInt(1, req.getUsuarioId());
                cs.setInt(2, req.getDetalleId());
                cs.setBigDecimal(3, req.getPrecioUnitario());
                if (req.getDescuentoLinea() != null) cs.setBigDecimal(4, req.getDescuentoLinea()); else cs.setNull(4, Types.DECIMAL);
                if (req.getCantidadPedida() != null) cs.setInt(5, req.getCantidadPedida()); else cs.setNull(5, Types.INTEGER);
                if (req.getLote() != null && !req.getLote().isBlank()) cs.setString(6, req.getLote()); else cs.setNull(6, Types.NVARCHAR);
                if (req.getFechaVencimiento() != null) cs.setDate(7, java.sql.Date.valueOf(req.getFechaVencimiento())); else cs.setNull(7, Types.DATE);

                cs.execute(); // consume RS
                return 1;
            } catch (SQLException ex) {
                throw new BadRequestException("Error al editar detalle: " + ex.getMessage());
            }
        });
    }

    // ========= QUITAR DETALLE =========
    public int quitarDetalle(int usuarioId, int detalleId) {
        String sql = "EXEC dbo.sp_COMPRAS_QuitarDetalleLinea @UsuarioId=?, @DetalleId=?";
        return jdbc.update(sql, usuarioId, detalleId);
    }

    // ========= ANULAR =========
    public int anular(CompraAnularRequest req) {
        String sql = "EXEC dbo.sp_COMPRAS_Anular @UsuarioId=?, @CompraId=?, @Motivo=?";
        return jdbc.update(sql, req.getUsuarioId(), req.getCompraId(), req.getMotivo());
    }

    // =====================================================================
    // =====================  C A T Á L O G O S  ===========================
    // =====================================================================

    /**
     * Ejecuta dbo.sp_COMPRAS_Lookups y devuelve SOLO el RS indicado:
     *   rsIndex = 0 → proveedores, 1 → bodegas, 2 → empleados
     */
    private List<Map<String,Object>> lookupsResultSet(int rsIndexWanted) {
        return jdbc.execute((Connection con) -> {
            List<Map<String,Object>> out = new ArrayList<>();
            try (CallableStatement cs = con.prepareCall("{call dbo.sp_COMPRAS_Lookups}")) {
                boolean has = cs.execute();
                int idx = 0;
                while (has) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (idx == rsIndexWanted && rs != null) {
                            ResultSetMetaData md = rs.getMetaData();
                            boolean hasCodigo = false;
                            int cc = md.getColumnCount();
                            for (int i = 1; i <= cc; i++) {
                                String label = md.getColumnLabel(i);
                                if ("codigo".equalsIgnoreCase(label)) { hasCodigo = true; break; }
                            }
                            while (rs.next()) {
                                Map<String,Object> m = new LinkedHashMap<>();
                                m.put("id", rs.getInt("id"));
                                // El SP usa nombre/nombre_completo según RS
                                String nombreCol = hasColumn(rs, "nombre") ? "nombre" :
                                        (hasColumn(rs, "nombre_completo") ? "nombre_completo" : "nombre");
                                m.put("nombre", rs.getString(nombreCol));
                                if (hasCodigo) { m.put("codigo", rs.getString("codigo")); }
                                // Campos opcionales (si alguna vez los agregas en el SP, no rompe)
                                if (hasColumn(rs, "nit")) m.put("nit", rs.getString("nit"));
                                if (hasColumn(rs, "activo")) m.put("activo", rs.getObject("activo"));
                                out.add(m);
                            }
                            // ya cargamos el RS deseado; salimos
                            break;
                        }
                    }
                    has = cs.getMoreResults();
                    idx++;
                }
            } catch (SQLException ex) {
                throw new BadRequestException("Error en lookups: " + ex.getMessage());
            }
            return out;
        });
    }

    /** Proveedores (usa RS #0 de sp_COMPRAS_Lookups). */
    public List<Map<String,Object>> catalogoProveedores(Boolean soloActivos) {
        // Nota: el filtro soloActivos lo maneja el SP (actualmente devuelve activos).
        List<Map<String,Object>> list = lookupsResultSet(0);
        if (soloActivos != null && !soloActivos) {
            // Si pidieran inactivos: no soportado por SP. Devolvemos tal cual.
            log.debug("catalogoProveedores: SP devuelve activos; soloActivos={} no filtra.", soloActivos);
        }
        return list;
    }

    /** Bodegas (usa RS #1 de sp_COMPRAS_Lookups). */
    public List<Map<String,Object>> catalogoBodegas() {
        return lookupsResultSet(1);
    }

    /** Empleados (usa RS #2 de sp_COMPRAS_Lookups). */
    public List<Map<String,Object>> catalogoEmpleados() {
        return lookupsResultSet(2);
    }

    /**
     * Catálogo de productos con búsqueda por texto (se mantiene como estaba).
     * Suele usarse para llenar el drop list de productos previo al autofill.
     */
    public List<Map<String,Object>> catalogoProductos(String texto, Integer limit) {
        String base = "SELECT TOP (?) id, nombre FROM dbo.productos ";
        String where = (texto != null && !texto.isBlank()) ? "WHERE nombre LIKE ? " : "";
        String order = "ORDER BY nombre ASC";
        return jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement(base + where + order);
            int idx = 1;
            ps.setInt(idx++, (limit != null && limit > 0) ? limit : 50);
            if (!where.isEmpty()) {
                String like = "%" + texto.trim() + "%";
                ps.setString(idx++, like);
            }
            return ps;
        }, (rs, rowNum) -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", rs.getInt("id"));
            m.put("nombre", rs.getString("nombre"));
            return m;
        });
    }

    // ========= AUTOFILL DE PRODUCTO (NUEVO) =========
    // Llama sp_PRODUCTOS_AutoFill para que, al elegir el producto en el drop list,
    // el front pueda autollenar unidad, precios, marca/categoría, y stock en bodega.
    public Map<String,Object> autoFillProducto(Integer productoId, Integer bodegaId) {
        return jdbc.execute((Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{call dbo.sp_PRODUCTOS_AutoFill(?, ?)}")) {
                if (productoId == null) throw new BadRequestException("productoId es requerido.");
                cs.setInt(1, productoId);
                if (bodegaId != null) cs.setInt(2, bodegaId); else cs.setNull(2, Types.INTEGER);

                try (ResultSet rs = cs.executeQuery()) {
                    if (!rs.next()) throw new ResourceNotFoundException("Producto no encontrado (id=" + productoId + ")");
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("producto_id", rs.getInt("producto_id"));
                    if (hasColumn(rs, "producto_codigo"))   m.put("producto_codigo", rs.getString("producto_codigo"));
                    if (hasColumn(rs, "producto_nombre"))   m.put("producto_nombre", rs.getString("producto_nombre"));
                    if (hasColumn(rs, "unidad_medida"))     m.put("unidad_medida", rs.getString("unidad_medida"));
                    if (hasColumn(rs, "precio_compra"))     m.put("precio_compra", rs.getBigDecimal("precio_compra"));
                    if (hasColumn(rs, "precio_venta"))      m.put("precio_venta", rs.getBigDecimal("precio_venta"));
                    if (hasColumn(rs, "marca_id"))          m.put("marca_id", getInt(rs, "marca_id"));
                    if (hasColumn(rs, "categoria_id"))      m.put("categoria_id", getInt(rs, "categoria_id"));
                    if (hasColumn(rs, "marca_nombre"))      m.put("marca_nombre", rs.getString("marca_nombre"));
                    if (hasColumn(rs, "categoria_nombre"))  m.put("categoria_nombre", rs.getString("categoria_nombre"));
                    if (hasColumn(rs, "stock_disponible"))  m.put("stock_disponible", rs.getObject("stock_disponible"));
                    return m;
                }
            } catch (SQLException ex) {
                throw new BadRequestException("Error en autoFillProducto: " + ex.getMessage());
            }
        });
    }

    // ========= (Quedan disponibles estos helpers por si otros componentes los usan) =========

    private Map<Integer,String> nombreByIds(String tableFqn, String nombreCol, Collection<Integer> ids) {
        Map<Integer,String> map = new HashMap<>();
        if (ids == null || ids.isEmpty()) return map;

        String placeholders = ids.stream().map(i -> "?").collect(Collectors.joining(","));
        String sql = "SELECT id, " + nombreCol + " AS nombre FROM " + tableFqn + " WHERE id IN (" + placeholders + ")";
        jdbc.query(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            int idx = 1;
            for (Integer id : ids) ps.setInt(idx++, id);
            return ps;
        }, rs -> {
            while (rs.next()) map.put(rs.getInt("id"), rs.getString("nombre"));
        });
        return map;
    }
}
