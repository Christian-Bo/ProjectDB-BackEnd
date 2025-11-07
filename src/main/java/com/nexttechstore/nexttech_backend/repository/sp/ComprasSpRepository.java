package com.nexttechstore.nexttech_backend.repository.sp;

import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.nexttechstore.nexttech_backend.exception.BadRequestException;
import com.nexttechstore.nexttech_backend.exception.ResourceNotFoundException;
import com.nexttechstore.nexttech_backend.model.compras.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

// ====== java.sql (importes explícitos, sin Date para evitar choque) ======
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

// ====== java.util (importes explícitos, aquí sí usamos java.util.Date) ======
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;            // <- usamos este Date (java.util.Date)
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repositorio de acceso a SPs de Compras + catálogos para combos.
 *
 * SPs utilizados:
 *  - dbo.sp_COMPRAS_Listar
 *  - dbo.sp_COMPRAS_ObtenerPorId
 *  - dbo.sp_COMPRAS_Crear
 *  - dbo.sp_COMPRAS_EditarCabecera
 *  - dbo.sp_COMPRAS_AgregarDetalle          (suma inventario por línea agregada)
 *  - dbo.sp_COMPRAS_EditarDetalleLinea      (ajusta inventario por delta)
 *  - dbo.sp_COMPRAS_QuitarDetalleLinea      (revierte inventario y elimina línea)
 *  - dbo.sp_COMPRAS_Anular                  (revierte inventario de toda la compra)
 *  - dbo.sp_PRODUCTOS_AutoFill
 *  - dbo.sp_COMPRAS_Lookups
 */
@Repository
public class ComprasSpRepository {

    private static final Logger log = LoggerFactory.getLogger(ComprasSpRepository.class);

    private final JdbcTemplate jdbc;

    public ComprasSpRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ===== Helpers de mapeo seguros =====

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

    // ===================== LISTAR =====================
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
                    if (hasColumn(rs, "proveedor_nombre")) it.setProveedorNombre(rs.getString("proveedor_nombre"));
                    if (hasColumn(rs, "bodega_destino_nombre")) it.setBodegaDestinoNombre(rs.getString("bodega_destino_nombre"));
                    return it;
                }
        );
    }

    // ===================== OBTENER POR ID =====================
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

                    if (cabecera == null) throw new ResourceNotFoundException("Compra no encontrada (id=" + compraId + ")");
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

    // ===================== CREAR (cabecera + TVP) =====================
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

    // ===================== EDITAR CABECERA =====================
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
                if (req.getDescuentoGeneral() != null) {
                    cs.setBigDecimal(9, req.getDescuentoGeneral().setScale(2, java.math.RoundingMode.HALF_UP));
                } else {
                    cs.setNull(9, Types.DECIMAL);
                }
                if (req.getObservaciones() != null && !req.getObservaciones().isBlank()) {
                    cs.setString(10, req.getObservaciones());
                } else {
                    cs.setNull(10, Types.NVARCHAR);
                }

                cs.execute(); // consume RS (no necesitamos mapear aquí)
                return 1;
            } catch (SQLException ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.contains("CK_compras_totales")) {
                    throw new BadRequestException(
                            "No se pudo guardar: los totales quedarían inválidos (revise que el descuento no exceda el subtotal y que el total no sea negativo)."
                    );
                }
                throw new BadRequestException("Error al editar cabecera: " + msg);
            }
        });
    }

    // ===================== AGREGAR DETALLE (1..n) =====================
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
                    boolean hasRs = cs.execute(); // SP devuelve líneas insertadas (con nombres)
                    while (hasRs) {
                        try (ResultSet rs = cs.getResultSet()) {
                            if (rs != null) {
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

    // ===================== EDITAR DETALLE (1 línea) =====================
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

                cs.execute(); // consume RS (línea actualizada)
                return 1;
            } catch (SQLException ex) {
                throw new BadRequestException("Error al editar detalle: " + ex.getMessage());
            }
        });
    }

    // ===================== QUITAR DETALLE =====================
    public int quitarDetalle(int usuarioId, int detalleId) {
        String sql = "EXEC dbo.sp_COMPRAS_QuitarDetalleLinea @UsuarioId=?, @DetalleId=?";
        return jdbc.update(sql, usuarioId, detalleId);
    }

    // ===================== ANULAR COMPRA =====================
    public int anular(CompraAnularRequest req) {
        String sql = "EXEC dbo.sp_COMPRAS_Anular @UsuarioId=?, @CompraId=?, @Motivo=?";
        return jdbc.update(sql, req.getUsuarioId(), req.getCompraId(), req.getMotivo());
    }

    // ===================== LOOKUPS (catálogos) =====================
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
                                String nombreCol = hasColumn(rs, "nombre") ? "nombre" :
                                        (hasColumn(rs, "nombre_completo") ? "nombre_completo" : "nombre");
                                m.put("nombre", rs.getString(nombreCol));
                                if (hasCodigo) { m.put("codigo", rs.getString("codigo")); }
                                if (hasColumn(rs, "nit")) m.put("nit", rs.getString("nit"));
                                if (hasColumn(rs, "activo")) m.put("activo", rs.getObject("activo"));
                                out.add(m);
                            }
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

    public List<Map<String,Object>> catalogoProveedores(Boolean soloActivos) {
        List<Map<String,Object>> list = lookupsResultSet(0);
        if (soloActivos != null && !soloActivos) {
            log.debug("catalogoProveedores: SP devuelve activos; soloActivos={} no filtra.", soloActivos);
        }
        return list;
    }

    public List<Map<String,Object>> catalogoBodegas() { return lookupsResultSet(1); }

    public List<Map<String,Object>> catalogoEmpleados() { return lookupsResultSet(2); }

    // ===================== CAT. PRODUCTOS SIMPLE =====================
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

    // ===================== AUTOFILL PRODUCTO =====================
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

    // ====== util opcional ======
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
