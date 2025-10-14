package com.nexttechstore.nexttech_backend.repository.sp;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.nexttechstore.nexttech_backend.dto.PagoRequestDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CxcSpRepository {

    private final DataSource dataSource;

    public CxcSpRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** sp_cxc_pagos_add → retorna pago_id */
    public int crearPago(Integer clienteId, BigDecimal monto, String formaPago, String observaciones) throws SQLException {
        String sql =
                "DECLARE @pago_id INT, @code INT, @msg NVARCHAR(200);\n" +
                        "EXEC dbo.sp_cxc_pagos_add @p_cliente_id=?, @p_monto_total=?, @p_forma_pago=?, @p_observaciones=?,\n" +
                        "  @out_pago_id=@pago_id OUTPUT, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT;\n" +
                        "SELECT @pago_id AS pago_id, @code AS code, @msg AS msg;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ps.setBigDecimal(2, monto);
            ps.setString(3, formaPago);
            ps.setString(4, observaciones);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Sin resultado de sp_cxc_pagos_add");
                int pagoId = rs.getInt("pago_id");
                int code   = rs.getInt("code");
                String msg = rs.getString("msg");
                if (code != 0) throw new SQLException("SP sp_cxc_pagos_add error ("+code+"): " + msg);
                if (pagoId <= 0) throw new SQLException("Pago no creado");
                return pagoId;
            }
        }
    }

    /** sp_cxc_aplicaciones_add con TVP dbo.tvp_cxc_aplicacion(documento_id, monto_aplicado) */
    public void aplicarPagoADocumentos(int pagoId, List<AplicacionItem> items) throws SQLException {
        if (items == null || items.isEmpty()) throw new SQLException("Debe proveer aplicaciones");

        SQLServerDataTable tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("documento_id", Types.INTEGER);
        tvp.addColumnMetadata("monto_aplicado", Types.DECIMAL);
        for (AplicacionItem it : items) {
            tvp.addRow(it.documentoId(), it.monto());
        }

        String sql =
                "DECLARE @code INT, @msg NVARCHAR(200);\n" +
                        "EXEC dbo.sp_cxc_aplicaciones_add @p_pago_id=?, @p_items=?, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT;\n" +
                        "SELECT @code AS code, @msg AS msg;";

        try (Connection conn = dataSource.getConnection()) {
            SQLServerConnection sqlConn = conn.unwrap(SQLServerConnection.class);
            try (SQLServerPreparedStatement ps = (SQLServerPreparedStatement) sqlConn.prepareStatement(sql)) {
                ps.setInt(1, pagoId);
                ps.setStructured(2, "dbo.tvp_cxc_aplicacion", tvp);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Sin resultado de sp_cxc_aplicaciones_add");
                    int code = rs.getInt("code");
                    String msg = rs.getString("msg");
                    if (code != 0) throw new SQLException("SP sp_cxc_aplicaciones_add error ("+code+"): " + msg);
                }
            }
        }
    }

    /** sp_cxc_pagos_anular */
    public void anularPago(int pagoId, String motivo) throws SQLException {
        String sql =
                "DECLARE @code INT, @msg NVARCHAR(200);\n" +
                        "EXEC dbo.sp_cxc_pagos_anular @p_pago_id=?, @p_motivo=?, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT;\n" +
                        "SELECT @code AS code, @msg AS msg;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pagoId);
            ps.setString(2, motivo);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Sin resultado de sp_cxc_pagos_anular");
                int code = rs.getInt("code");
                String msg = rs.getString("msg");
                if (code != 0) throw new SQLException("SP sp_cxc_pagos_anular error ("+code+"): " + msg);
            }
        }
    }

    /** sp_cxc_estado_cuenta → devuelve rows con el SELECT del SP */
    public List<EstadoCuentaRow> estadoDeCuenta(int clienteId, Date desde, Date hasta) throws SQLException {
        String sql =
                "DECLARE @code INT, @msg NVARCHAR(200);\n" +
                        "EXEC dbo.sp_cxc_estado_cuenta @p_cliente_id=?, @p_fecha_ini=?, @p_fecha_fin=?, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            if (desde != null) ps.setDate(2, desde); else ps.setNull(2, Types.DATE);
            if (hasta != null) ps.setDate(3, hasta); else ps.setNull(3, Types.DATE);

            List<EstadoCuentaRow> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EstadoCuentaRow row = new EstadoCuentaRow(
                            rs.getInt("documento_id"),
                            rs.getString("origen_tipo"),
                            rs.getInt("origen_id"),
                            rs.getString("numero_documento"),
                            rs.getDate("fecha_emision"),
                            rs.getDate("fecha_vencimiento"),
                            rs.getBigDecimal("monto_total"),
                            rs.getBigDecimal("saldo_pendiente"),
                            rs.getString("estado")
                    );
                    out.add(row);
                }
            }
            return out;
        }
    }

    // -------- DTOs internos simples para este repo --------
    public record AplicacionItem(int documentoId, BigDecimal monto) {}
    public record EstadoCuentaRow(
            int documentoId, String origenTipo, int origenId, String numeroDocumento,
            Date fechaEmision, Date fechaVencimiento,
            java.math.BigDecimal montoTotal, java.math.BigDecimal saldoPendiente,
            String estado) {}
}
