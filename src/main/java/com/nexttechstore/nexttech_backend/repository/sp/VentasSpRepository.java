package com.nexttechstore.nexttech_backend.repository.sp;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.nexttechstore.nexttech_backend.dto.VentaDetalleEditItemDto;
import com.nexttechstore.nexttech_backend.dto.VentaHeaderEditDto;
import com.nexttechstore.nexttech_backend.dto.VentaItemDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VentasSpRepository {

    private final DataSource dataSource;

    public VentasSpRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ===== Helpers para leer columnas con alias distintos =====
    private Integer readIntByAnyLabel(ResultSet rs, String... labels) throws SQLException {
        for (String l : labels) {
            try { return rs.getInt(l); } catch (SQLException ignore) {}
        }
        throw new SQLException("No se encontró ninguna de las columnas: " + String.join(",", labels));
    }
    private String readStringByAnyLabel(ResultSet rs, String... labels) throws SQLException {
        for (String l : labels) {
            try { return rs.getString(l); } catch (SQLException ignore) {}
        }
        throw new SQLException("No se encontró ninguna de las columnas: " + String.join(",", labels));
    }

    /** Crea venta vía SP: dbo.sp_ventas_create (usa TVP v2 con lote/vence) */
    public int crearVenta(VentaRequestDto req) throws SQLException {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new SQLException("La venta debe tener al menos 1 ítem");
        }
        if (req.getBodegaOrigenId() == null) {
            throw new SQLException("Bodega de origen requerida");
        }
        if (req.getSerieId() == null) {
            throw new SQLException("Serie requerida");
        }

        Integer bodegaOrigen = req.getBodegaOrigenId();
        Integer vendedorId   = (req.getVendedorId() != null) ? req.getVendedorId() : req.getUsuarioId();
        Integer cajeroId     = (req.getCajeroId()   != null) ? req.getCajeroId()   : req.getUsuarioId();
        String  tipoPago     = (req.getTipoPago()   != null && !req.getTipoPago().isBlank()) ? req.getTipoPago() : "C";
        boolean esCredito    = "R".equalsIgnoreCase(tipoPago);

        // TVP detalle v2 (DECIMAL(19,4))
        SQLServerDataTable tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("producto_id", Types.INTEGER);
        tvp.addColumnMetadata("cantidad", Types.INTEGER);
        tvp.addColumnMetadata("precio_unitario", Types.DECIMAL);
        tvp.addColumnMetadata("descuento_linea", Types.DECIMAL);
        tvp.addColumnMetadata("lote", Types.NVARCHAR);
        tvp.addColumnMetadata("fecha_vencimiento", Types.DATE);

        for (VentaItemDto it : req.getItems()) {
            Integer cantInt = (it.getCantidad() != null) ? it.getCantidad().intValue() : null;
            if (cantInt == null) throw new SQLException("Cantidad requerida");
            if (cantInt < 1)     throw new SQLException("Cantidad debe ser mayor a 0");

            BigDecimal precio = it.getPrecioUnitario() != null ? it.getPrecioUnitario().setScale(4, BigDecimal.ROUND_HALF_UP) : null;
            if (precio == null) throw new SQLException("Precio unitario requerido");

            BigDecimal desc = it.getDescuento() != null ? it.getDescuento().setScale(4, BigDecimal.ROUND_HALF_UP) : null;
            java.sql.Date vence = (it.getFechaVencimiento() != null) ? java.sql.Date.valueOf(it.getFechaVencimiento()) : null;

            tvp.addRow(
                    it.getProductoId(),
                    cantInt,
                    precio,
                    desc,
                    it.getLote(),
                    vence
            );
        }

        // Nota: tu SP puede terminar con:
        //   SELECT @out_venta_id AS out_venta_id, @out_status_code AS code, @out_message AS msg;
        // o con:
        //   SELECT code = @out_status_code, message = @out_message, id = @out_venta_id;
        // Este repo soporta ambas variantes.
        String sql =
                "DECLARE @out_venta_id INT, @out_status_code INT, @out_message NVARCHAR(200); \n" +
                        "EXEC dbo.sp_ventas_create \n" +
                        "  @p_cliente_id=?, @p_bodega_origen_id=?, @p_serie_id=?, @p_vendedor_id=?, @p_cajero_id=?, \n" +
                        "  @p_tipo_pago=?, @p_descuento_general=?, @p_iva=?, @p_es_credito=?, @p_detalle=?, \n" +
                        "  @out_venta_id=@out_venta_id OUTPUT, @out_status_code=@out_status_code OUTPUT, @out_message=@out_message OUTPUT; \n" +
                        "SELECT @out_venta_id AS out_venta_id, @out_status_code AS code, @out_message AS msg;"; // fallback si el SP no hace SELECT al final

        try (Connection conn = dataSource.getConnection()) {
            SQLServerConnection sqlConn = conn.unwrap(SQLServerConnection.class);
            try (SQLServerPreparedStatement ps = (SQLServerPreparedStatement) sqlConn.prepareStatement(sql)) {
                ps.setInt(1,  req.getClienteId());
                ps.setInt(2,  bodegaOrigen);
                ps.setInt(3,  req.getSerieId());
                ps.setInt(4,  vendedorId);
                if (cajeroId == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, cajeroId);
                ps.setString(6, tipoPago);
                ps.setBigDecimal(7, BigDecimal.ZERO.setScale(4)); // descuento_general
                ps.setBigDecimal(8, BigDecimal.ZERO.setScale(4)); // iva
                ps.setBoolean(9, esCredito);
                ps.setStructured(10, "dbo.tvp_venta_detalle_v2", tvp);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Sin resultado de sp_ventas_create");

                    // Lee por cualquiera de los alias habituales
                    int outId  = 0;
                    int code   = 0;
                    String msg = null;
                    try {
                        outId = readIntByAnyLabel(rs, "out_venta_id", "id", "venta_id");
                        code  = readIntByAnyLabel(rs, "code", "status", "status_code");
                        msg   = readStringByAnyLabel(rs, "msg", "message");
                    } catch (SQLException incompatible) {
                        // Si tu SP devolvió otro SELECT al final, avanza y vuelve a intentar
                        while (ps.getMoreResults()) {
                            try (ResultSet rs2 = ps.getResultSet()) {
                                if (rs2 != null && rs2.next()) {
                                    outId = readIntByAnyLabel(rs2, "out_venta_id", "id", "venta_id");
                                    code  = readIntByAnyLabel(rs2, "code", "status", "status_code");
                                    msg   = readStringByAnyLabel(rs2, "msg", "message");
                                    break;
                                }
                            }
                        }
                    }

                    if (code != 0) throw new SQLException("sp_ventas_create ("+code+"): " + msg);
                    if (outId <= 0) throw new SQLException("sp_ventas_create no devolvió id válido");
                    return outId;
                }
            }
        }
    }

    /** Anula venta: sp_ventas_anular */
    public void anularVenta(int ventaId, String motivo) throws SQLException {
        String sql =
                "DECLARE @code INT, @msg NVARCHAR(200);\n" +
                        "EXEC dbo.sp_ventas_anular @p_venta_id=?, @p_motivo=?, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT;\n" +
                        "SELECT @code AS code, @msg AS msg;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ventaId);
            ps.setString(2, motivo);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Sin resultado de sp_ventas_anular");
                int code = rs.getInt("code");
                String msg = rs.getString("msg");
                if (code != 0) throw new SQLException("sp_ventas_anular ("+code+"): " + msg);
            }
        }
    }

    /** Edita cabecera: dbo.sp_ventas_edit_header */
    public void editarHeader(int ventaId, VentaHeaderEditDto dto) throws SQLException {
        String sql =
                "DECLARE @out_status INT, @out_msg NVARCHAR(200); " +
                        "EXEC dbo.sp_ventas_edit_header " +
                        "  @p_venta_id=?, @p_cliente_id=?, @p_tipo_pago=?, @p_vendedor_id=?, @p_cajero_id=?, @p_observaciones=?, " +
                        "  @out_status_code=@out_status OUTPUT, @out_message=@out_msg OUTPUT; " +
                        "SELECT @out_status AS status_code, @out_msg AS message;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ventaId);
            if (dto.getClienteId() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, dto.getClienteId());
            if (dto.getTipoPago() == null)  ps.setNull(3, Types.CHAR);    else ps.setString(3, dto.getTipoPago());
            if (dto.getVendedorId() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, dto.getVendedorId());
            if (dto.getCajeroId() == null)   ps.setNull(5, Types.INTEGER); else ps.setInt(5, dto.getCajeroId());
            if (dto.getObservaciones() == null) ps.setNull(6, Types.NVARCHAR); else ps.setString(6, dto.getObservaciones());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int code = rs.getInt("status_code");
                    String msg = rs.getString("message");
                    if (code != 0) throw new SQLException(msg != null ? msg : "sp_ventas_edit_header error");
                } else {
                    throw new SQLException("sp_ventas_edit_header sin resultado");
                }
            }
        }
    }

    /** Edita detalle: dbo.sp_ventas_edit_detalle (usa TVP v2 con lote/vence) */
    public void editarDetalle(int ventaId, List<VentaDetalleEditItemDto> items) throws SQLException {
        SQLServerDataTable tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("detalle_id", Types.INTEGER);
        tvp.addColumnMetadata("producto_id", Types.INTEGER);
        tvp.addColumnMetadata("bodega_id", Types.INTEGER);
        tvp.addColumnMetadata("cantidad", Types.INTEGER);
        tvp.addColumnMetadata("precio_unitario", Types.DECIMAL);
        tvp.addColumnMetadata("descuento_linea", Types.DECIMAL);
        tvp.addColumnMetadata("accion", Types.CHAR);
        tvp.addColumnMetadata("lote", Types.NVARCHAR);
        tvp.addColumnMetadata("fecha_vencimiento", Types.DATE);

        for (VentaDetalleEditItemDto it : items) {
            java.sql.Date vence = (it.getFechaVencimiento() != null) ? java.sql.Date.valueOf(it.getFechaVencimiento()) : null;
            BigDecimal precio = it.getPrecioUnitario() != null ? it.getPrecioUnitario().setScale(4, BigDecimal.ROUND_HALF_UP) : null;
            BigDecimal desc   = it.getDescuentoLinea() != null ? it.getDescuentoLinea().setScale(4, BigDecimal.ROUND_HALF_UP) : null;

            tvp.addRow(
                    it.getDetalleId(),
                    it.getProductoId(),
                    it.getBodegaId(),
                    it.getCantidad(),
                    precio,
                    desc,
                    it.getAccion(),
                    it.getLote(),
                    vence
            );
        }

        String sql =
                "DECLARE @out_status INT, @out_msg NVARCHAR(200); " +
                        "EXEC dbo.sp_ventas_edit_detalle " +
                        "  @p_venta_id=?, @p_items=?, @out_status_code=@out_status OUTPUT, @out_message=@out_msg OUTPUT; " +
                        "SELECT @out_status AS status_code, @out_msg AS message;";

        try (Connection conn = dataSource.getConnection()) {
            SQLServerConnection sqlConn = conn.unwrap(SQLServerConnection.class);
            try (SQLServerPreparedStatement ps = (SQLServerPreparedStatement) sqlConn.prepareStatement(sql)) {
                ps.setInt(1, ventaId);
                ps.setStructured(2, "dbo.tvp_venta_detalle_edit_v2", tvp);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int code = rs.getInt("status_code");
                        String msg = rs.getString("message");
                        if (code != 0) throw new SQLException(msg != null ? msg : "sp_ventas_edit_detalle error");
                    } else {
                        throw new SQLException("sp_ventas_edit_detalle sin resultado");
                    }
                }
            }
        }
    }

    /** GET by id */
    public Map<String, Object> getVentaById(int ventaId) throws SQLException {
        String sql = "EXEC dbo.sp_ventas_get_by_id @p_venta_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ventaId);

            Map<String, Object> header = null;
            var detalle = new ArrayList<Map<String,Object>>();

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    header = new HashMap<>();
                    header.put("id", rs.getInt("id"));
                    header.put("numero_venta", rs.getString("numero_venta"));
                    header.put("fecha_venta", rs.getDate("fecha_venta"));
                    header.put("subtotal", rs.getBigDecimal("subtotal"));
                    header.put("descuento_general", rs.getBigDecimal("descuento_general"));
                    header.put("iva", rs.getBigDecimal("iva"));
                    header.put("total", rs.getBigDecimal("total"));
                    header.put("estado", rs.getString("estado"));
                    header.put("tipo_pago", rs.getString("tipo_pago"));
                    header.put("observaciones", rs.getString("observaciones"));
                    header.put("cliente_id", rs.getInt("cliente_id"));
                    header.put("cliente_codigo", rs.getString("cliente_codigo"));
                    header.put("cliente_nombre", rs.getString("cliente_nombre"));
                    header.put("vendedor_id", rs.getInt("vendedor_id"));
                    header.put("cajero_id", rs.getInt("cajero_id"));
                    header.put("bodega_origen_id", rs.getInt("bodega_origen_id"));
                }
            }

            if (ps.getMoreResults()) {
                try (ResultSet rs2 = ps.getResultSet()) {
                    while (rs2.next()) {
                        Map<String,Object> row = new HashMap<>();
                        row.put("detalle_id", rs2.getInt("detalle_id"));
                        row.put("producto_id", rs2.getInt("producto_id"));
                        row.put("producto_codigo", rs2.getString("producto_codigo"));
                        row.put("producto_nombre", rs2.getString("producto_nombre"));
                        row.put("cantidad", rs2.getInt("cantidad"));
                        row.put("precio_unitario", rs2.getBigDecimal("precio_unitario"));
                        row.put("descuento_linea", rs2.getBigDecimal("descuento_linea"));
                        row.put("subtotal", rs2.getBigDecimal("subtotal"));
                        row.put("lote", rs2.getString("lote"));
                        row.put("fecha_vencimiento", rs2.getDate("fecha_vencimiento"));
                        detalle.add(row);
                    }
                }
            }

            if (header == null) {
                throw new SQLException("Venta no encontrada id=" + ventaId);
            }
            Map<String,Object> out = new HashMap<>();
            out.put("header", header);
            out.put("detalle", detalle);
            return out;
        }
    }

    /** Listar */
    public List<Map<String,Object>> listarVentas(
            java.time.LocalDate desde,
            java.time.LocalDate hasta,
            Integer clienteId,
            String numeroVenta,
            Boolean incluirAnuladas,
            Integer page,
            Integer size
    ) throws SQLException {
        String sql = "EXEC dbo.sp_ventas_list " +
                "@p_desde=?, @p_hasta=?, @p_cliente_id=?, @p_numero_venta=?, " +
                "@p_incluir_anuladas=?, @p_page=?, @p_size=?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (desde != null) ps.setDate(1, java.sql.Date.valueOf(desde)); else ps.setNull(1, Types.DATE);
            if (hasta != null) ps.setDate(2, java.sql.Date.valueOf(hasta)); else ps.setNull(2, Types.DATE);
            if (clienteId != null) ps.setInt(3, clienteId); else ps.setNull(3, Types.INTEGER);
            if (numeroVenta != null && !numeroVenta.isBlank()) ps.setString(4, numeroVenta); else ps.setNull(4, Types.NVARCHAR);
            ps.setBoolean(5, incluirAnuladas != null ? incluirAnuladas : false);
            ps.setInt(6, page != null ? page : 0);
            ps.setInt(7, size != null ? size : 50);

            var list = new ArrayList<Map<String,Object>>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("numero_venta", rs.getString("numero_venta"));
                    row.put("fecha_venta", rs.getDate("fecha_venta"));
                    row.put("total", rs.getBigDecimal("total"));
                    row.put("cliente_id", rs.getInt("cliente_id"));
                    row.put("cliente_nombre", rs.getString("cliente_nombre"));
                    row.put("estado", rs.getString("estado"));
                    row.put("tipo_pago", rs.getString("tipo_pago"));
                    list.add(row);
                }
            }
            return list;
        }
    }
}
