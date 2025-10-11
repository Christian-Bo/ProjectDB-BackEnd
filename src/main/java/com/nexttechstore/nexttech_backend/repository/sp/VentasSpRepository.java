package com.nexttechstore.nexttech_backend.repository.sp;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.nexttechstore.nexttech_backend.dto.VentaItemDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Repository
public class VentasSpRepository {

    private final DataSource dataSource;

    public VentasSpRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * EXEC dbo.sp_ventas_registrar @UsuarioId, @ClienteId, @SerieId, @Items (TVP), outputs
     * Retorna OutVentaId; si el SP devuelve mensaje de error, lanza SQLException.
     */
    public int registrarVenta(VentaRequestDto req) throws SQLException {
        // TVP: dbo.tvp_ventas_items(producto_id, bodega_id, cantidad, precio_unitario, descuento, impuesto)
        SQLServerDataTable tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("producto_id", Types.INTEGER);
        tvp.addColumnMetadata("bodega_id", Types.INTEGER);
        tvp.addColumnMetadata("cantidad", Types.DECIMAL);
        tvp.addColumnMetadata("precio_unitario", Types.DECIMAL);
        tvp.addColumnMetadata("descuento", Types.DECIMAL);
        tvp.addColumnMetadata("impuesto", Types.DECIMAL);

        for (VentaItemDto it : req.getItems()) {
            tvp.addRow(
                    it.getProductoId(),
                    it.getBodegaId(),
                    it.getCantidad(),
                    it.getPrecioUnitario(),
                    it.getDescuento(),
                    it.getImpuesto()
            );
        }

        String sql =
                "DECLARE @OutVentaId INT, @OutMsg NVARCHAR(200); " +
                        "EXEC dbo.sp_ventas_registrar " +
                        "  @UsuarioId=?, @ClienteId=?, @SerieId=?, @Items=?, " +
                        "  @OutVentaId=@OutVentaId OUTPUT, @OutMensaje=@OutMsg OUTPUT; " +
                        "SELECT @OutVentaId AS OutVentaId, @OutMsg AS OutMensaje;";

        try (Connection conn = dataSource.getConnection()) {
            SQLServerConnection sqlConn = conn.unwrap(SQLServerConnection.class);
            try (SQLServerPreparedStatement ps = (SQLServerPreparedStatement) sqlConn.prepareStatement(sql)) {
                ps.setInt(1, req.getUsuarioId());
                ps.setInt(2, req.getClienteId());
                ps.setInt(3, req.getSerieId());
                ps.setStructured(4, "dbo.tvp_ventas_items", tvp);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Sin resultado de sp_ventas_registrar");
                    int outId = rs.getInt("OutVentaId");
                    String outMsg = rs.getString("OutMensaje");
                    if (outId <= 0) throw new SQLException(outMsg != null ? outMsg : "Fallo al crear venta");
                    return outId;
                }
            }
        }
    }
}
