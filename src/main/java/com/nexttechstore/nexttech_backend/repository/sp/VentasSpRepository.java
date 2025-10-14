package com.nexttechstore.nexttech_backend.repository.sp;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.nexttechstore.nexttech_backend.dto.VentaItemDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Types;

@Repository
public class VentasSpRepository {

    private final DataSource dataSource;

    public VentasSpRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Llama a: dbo.sp_ventas_create
     * Params:
     *  @p_cliente_id, @p_bodega_origen_id, @p_serie_id, @p_vendedor_id, @p_cajero_id,
     *  @p_tipo_pago, @p_descuento_general, @p_iva, @p_es_credito, @p_detalle (TVP),
     *  OUT: @out_id, @out_status_code, @out_message
     */
    public int crearVenta(VentaRequestDto req) throws SQLException {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new SQLException("La venta debe tener al menos 1 ítem");
        }

        Integer bodegaOrigen = (req.getBodegaOrigenId() != null)
                ? req.getBodegaOrigenId()
                : req.getItems().get(0).getBodegaId();
        Integer vendedorId = (req.getVendedorId() != null) ? req.getVendedorId() : req.getUsuarioId();
        Integer cajeroId   = (req.getCajeroId()   != null) ? req.getCajeroId()   : req.getUsuarioId();
        String  tipoPago   = (req.getTipoPago()   != null && !req.getTipoPago().isBlank()) ? req.getTipoPago() : "C";
        boolean esCredito  = "R".equalsIgnoreCase(tipoPago);

        java.math.BigDecimal descuentoGeneral = java.math.BigDecimal.ZERO;
        java.math.BigDecimal iva = java.math.BigDecimal.ZERO;

        // TVP dbo.tvp_venta_detalle(producto_id, cantidad, precio_unitario, descuento_linea)
        SQLServerDataTable tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("producto_id", Types.INTEGER);
        tvp.addColumnMetadata("cantidad", Types.DECIMAL);
        tvp.addColumnMetadata("precio_unitario", Types.DECIMAL);
        tvp.addColumnMetadata("descuento_linea", Types.DECIMAL);

        for (VentaItemDto it : req.getItems()) {
            java.math.BigDecimal desc = it.getDescuento() != null ? it.getDescuento() : java.math.BigDecimal.ZERO;
            tvp.addRow(it.getProductoId(), it.getCantidad(), it.getPrecioUnitario(), desc);
        }

        String sql =
                "DECLARE @out_venta_id INT, @out_status_code INT, @out_message NVARCHAR(200); \n" +
                        "EXEC dbo.sp_ventas_create \n" +
                        "  @p_cliente_id=?, @p_bodega_origen_id=?, @p_serie_id=?, @p_vendedor_id=?, @p_cajero_id=?, \n" +
                        "  @p_tipo_pago=?, @p_descuento_general=?, @p_iva=?, @p_es_credito=?, @p_detalle=?, \n" +
                        "  @out_venta_id=@out_venta_id OUTPUT, @out_status_code=@out_status_code OUTPUT, @out_message=@out_message OUTPUT; \n" +
                        "SELECT @out_venta_id AS out_venta_id, @out_status_code AS code, @out_message AS msg;";

        try (Connection conn = dataSource.getConnection()) {
            SQLServerConnection sqlConn = conn.unwrap(SQLServerConnection.class);
            try (SQLServerPreparedStatement ps = (SQLServerPreparedStatement) sqlConn.prepareStatement(sql)) {
                ps.setInt(1,  req.getClienteId());
                ps.setInt(2,  bodegaOrigen);
                ps.setInt(3,  req.getSerieId());
                ps.setInt(4,  vendedorId);
                ps.setObject(5, cajeroId, Types.INTEGER);
                ps.setString(6, tipoPago);
                ps.setBigDecimal(7, descuentoGeneral);
                ps.setBigDecimal(8, iva);
                ps.setBoolean(9, esCredito);
                ps.setStructured(10, "dbo.tvp_venta_detalle", tvp);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Sin resultado de sp_ventas_create");
                    int outId   = rs.getInt("out_venta_id");  // <-- nombre correcto
                    int code    = rs.getInt("code");
                    String msg  = rs.getString("msg");
                    if (code != 0) throw new SQLException("SP sp_ventas_create error ("+code+"): " + msg);
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
                if (code != 0) throw new SQLException("SP sp_ventas_anular error ("+code+"): " + msg);
            }
        }
    }
}
