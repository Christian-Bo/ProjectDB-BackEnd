package com.nexttechstore.nexttech_backend.repository.sp;

import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.nexttechstore.nexttech_backend.exception.BadRequestException;
import com.nexttechstore.nexttech_backend.exception.ResourceNotFoundException;
import com.nexttechstore.nexttech_backend.model.compras.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

// ====== IMPORTS JDBC EXPLÍCITOS (evitar java.sql.* para no chocar con java.util.Date) ======
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

// ====== FECHAS Y COLECCIONES ======
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;           // Este es java.util.Date (firma pública)
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Repositorio de acceso a SPs de Compras.
 *
 * Mantén los nombres de SPs y el tipo de TVP aquí en un solo lugar:
 *  - SP Listar:            dbo.sp_COMPRAS_Listar
 *  - SP ObtenerPorId:      dbo.sp_COMPRAS_ObtenerPorId (devuelve 2 RS: cabecera y detalle)
 *  - SP Crear:             dbo.sp_COMPRAS_Crear (usa TVP dbo.tvp_DetalleCompra y OUT @CompraIdOut)
 *  - SP EditarCabecera:    dbo.sp_COMPRAS_EditarCabecera
 *  - SP AgregarDetalle:    dbo.sp_COMPRAS_AgregarDetalle (TVP)
 *  - SP EditarDetalle:     dbo.sp_COMPRAS_EditarDetalleLinea
 *  - SP QuitarDetalle:     dbo.sp_COMPRAS_QuitarDetalleLinea
 *  - SP Anular:            dbo.sp_COMPRAS_Anular
 *
 * Si cambias columnas devueltas por los SPs, ajusta los mappers (mapCabecera/mapDetalle)
 * y/o las columnas del TVP en crear()/agregarDetalle().
 */
@Repository
public class ComprasSpRepository {

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

    // ========= LISTAR =========

    public List<CompraListItem> listar(Date fechaDel, Date fechaAl, Integer proveedorId, String estado, String texto) {
        List<CompraListItem> base = jdbc.query(
                "EXEC dbo.sp_COMPRAS_Listar @FechaDel=?, @FechaAl=?, @ProveedorId=?, @Estado=?, @Texto=?",
                ps -> {
                    // convertir java.util.Date -> java.sql.Date explícito
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
                    return it;
                }
        );

        if (base.isEmpty()) return base;

        // Resolver nombres por lotes (evita N+1)
        Set<Integer> provIds = base.stream().map(CompraListItem::getProveedorId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Integer> bodIds  = base.stream().map(CompraListItem::getBodegaDestinoId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Integer,String> provMap = nombreByIds("dbo.proveedores", "nombre", provIds);
        Map<Integer,String> bodMap  = nombreByIds("dbo.bodegas",    "nombre", bodIds);

        for (CompraListItem it : base) {
            it.setProveedorNombre(provMap.get(it.getProveedorId()));
            it.setBodegaDestinoNombre(bodMap.get(it.getBodegaDestinoId()));
        }
        return base;
    }

    // ========= OBTENER POR ID =========

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
                                    if (rs.next()) cabecera = mapCabecera(rs);
                                } else if (rsIndex == 1) {
                                    while (rs.next()) detalle.add(mapDetalle(rs));
                                }
                            }
                        }
                        hasResults = cs.getMoreResults();
                        rsIndex++;
                    }

                    if (cabecera == null) {
                        throw new ResourceNotFoundException("Compra no encontrada (id=" + compraId + ")");
                    }

                    // Nombres de cabecera
                    if (cabecera.getProveedorId() != null) {
                        cabecera.setProveedorNombre(nombreById("dbo.proveedores", "nombre", cabecera.getProveedorId()));
                    }
                    if (cabecera.getBodegaDestinoId() != null) {
                        cabecera.setBodegaDestinoNombre(nombreById("dbo.bodegas", "nombre", cabecera.getBodegaDestinoId()));
                    }
                    if (cabecera.getEmpleadoCompradorId() != null) {
                        cabecera.setEmpleadoCompradorNombre(nombreEmpleado(cabecera.getEmpleadoCompradorId()));
                    }
                    if (cabecera.getEmpleadoAutorizaId() != null) {
                        cabecera.setEmpleadoAutorizaNombre(nombreEmpleado(cabecera.getEmpleadoAutorizaId()));
                    }

                    // Nombres de productos (lote)
                    Set<Integer> prodIds = detalle.stream().map(CompraDetalle::getProductoId).filter(Objects::nonNull).collect(Collectors.toSet());
                    Map<Integer,String> prodMap = nombreByIds("dbo.productos", "nombre", prodIds);
                    for (CompraDetalle li : detalle) {
                        li.setProductoNombre(prodMap.get(li.getProductoId()));
                    }

                    return new CompraFull(cabecera, detalle);
                }
            } catch (SQLException ex) {
                throw new BadRequestException("Error al obtener compra: " + ex.getMessage());
            }
        });
    }

    private CompraCabecera mapCabecera(ResultSet rs) throws SQLException {
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
        return c;
    }

    private CompraDetalle mapDetalle(ResultSet rs) throws SQLException {
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
        return d;
    }

    // ========= CREAR (TVP + OUT) =========

    public int crear(CompraCrearRequest req) {
        return jdbc.execute((Connection con) -> {
            try {
                SQLServerConnection sqlCon = con.unwrap(SQLServerConnection.class);
                String call = "{call dbo.sp_COMPRAS_Crear(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                try (SQLServerCallableStatement cs = (SQLServerCallableStatement) sqlCon.prepareCall(call)) {

                    cs.setInt(1,  req.getUsuarioId());
                    cs.setString(2, req.getNumeroCompra());
                    cs.setString(3, req.getNoFacturaProveedor());
                    cs.setDate(4,  java.sql.Date.valueOf(req.getFechaCompra()));   // usar java.sql.Date
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
                    cs.execute();

                    return cs.getInt(11);
                }
            } catch (SQLException ex) {
                throw new BadRequestException("Error al crear compra: " + ex.getMessage());
            }
        });
    }

    // ========= EDITAR CABECERA =========

    public int editarCabecera(CompraEditarCabeceraRequest req) {
        String sql = "EXEC dbo.sp_COMPRAS_EditarCabecera @UsuarioId=?, @CompraId=?, @NoFacturaProveedor=?, @FechaCompra=?, @ProveedorId=?, @EmpleadoCompradorId=?, @EmpleadoAutorizaId=?, @BodegaDestinoId=?, @DescuentoGeneral=?, @Observaciones=?";
        return jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, req.getUsuarioId());
            ps.setInt(2, req.getCompraId());
            ps.setString(3, req.getNoFacturaProveedor());
            ps.setDate(4, java.sql.Date.valueOf(req.getFechaCompra()));  // java.sql.Date explícito
            ps.setInt(5, req.getProveedorId());
            ps.setInt(6, req.getEmpleadoCompradorId());
            if (req.getEmpleadoAutorizaId() != null) ps.setInt(7, req.getEmpleadoAutorizaId()); else ps.setNull(7, Types.INTEGER);
            ps.setInt(8, req.getBodegaDestinoId());
            if (req.getDescuentoGeneral() != null) ps.setBigDecimal(9, BigDecimal.valueOf(req.getDescuentoGeneral())); else ps.setNull(9, Types.DECIMAL);
            if (req.getObservaciones() != null && !req.getObservaciones().isBlank()) ps.setString(10, req.getObservaciones()); else ps.setNull(10, Types.NVARCHAR);
            return ps;
        });
    }

    // ========= AGREGAR DETALLE (1..n) =========

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
                    return cs.executeUpdate();
                }
            } catch (SQLException ex) {
                throw new BadRequestException("Error al agregar detalle: " + ex.getMessage());
            }
        });
    }

    // ========= EDITAR DETALLE (1 línea) =========

    public int editarDetalle(CompraEditarDetalleRequest req) {
        String sql = "EXEC dbo.sp_COMPRAS_EditarDetalleLinea @UsuarioId=?, @DetalleId=?, @PrecioUnitario=?, @DescuentoLinea=?, @CantidadPedida=?, @Lote=?, @FechaVencimiento=?";
        return jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, req.getUsuarioId());
            ps.setInt(2, req.getDetalleId());
            ps.setBigDecimal(3, req.getPrecioUnitario());
            if (req.getDescuentoLinea() != null) ps.setBigDecimal(4, req.getDescuentoLinea()); else ps.setNull(4, Types.DECIMAL);
            if (req.getCantidadPedida() != null) ps.setInt(5, req.getCantidadPedida()); else ps.setNull(5, Types.INTEGER);
            if (req.getLote() != null && !req.getLote().isBlank()) ps.setString(6, req.getLote()); else ps.setNull(6, Types.NVARCHAR);
            if (req.getFechaVencimiento() != null) ps.setDate(7, java.sql.Date.valueOf(req.getFechaVencimiento())); else ps.setNull(7, Types.DATE);
            return ps;
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

    // ========= Mini-DAO para resolver nombres legibles =========

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

    private String nombreById(String tableFqn, String nombreCol, int id) {
        return jdbc.query(
                "SELECT " + nombreCol + " AS nombre FROM " + tableFqn + " WHERE id=?",
                ps -> ps.setInt(1, id),
                rs -> rs.next() ? rs.getString("nombre") : null
        );
    }

    private String nombreEmpleado(int empleadoId) {
        return jdbc.query(
                "SELECT (nombres + ' ' + apellidos) AS nombre FROM dbo.empleados WHERE id=?",
                ps -> ps.setInt(1, empleadoId),
                rs -> rs.next() ? rs.getString("nombre") : null
        );
    }
}
