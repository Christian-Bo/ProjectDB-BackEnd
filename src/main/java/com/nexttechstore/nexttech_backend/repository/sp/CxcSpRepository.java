package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.cxc.CxcAplicacionItemDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
public class CxcSpRepository {
    private final DataSource dataSource;
    public CxcSpRepository(DataSource dataSource){ this.dataSource = dataSource; }

    /**
     * Crea un pago (no aplica). Mapea a: dbo.sp_cxc_pagos_add
     * NOTA: tu SP NO recibe fecha. La asigna internamente (SYSUTCDATETIME()).
     */
    public int crearPago(int clienteId, java.math.BigDecimal monto, String formaPago, java.sql.Date /*ignorado*/ fecha, String obs)
            throws SQLException {

        String sql =
                "DECLARE @pago_id INT, @code INT, @msg NVARCHAR(200); " +
                        "EXEC dbo.sp_cxc_pagos_add " +
                        "  @p_cliente_id=?, @p_monto_total=?, @p_forma_pago=?, @p_observaciones=?, " +
                        "  @out_pago_id=@pago_id OUTPUT, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT; " +
                        "SELECT @pago_id AS pago_id, @code AS code, @msg AS msg;";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ps.setBigDecimal(2, monto);
            if (formaPago == null || formaPago.isBlank()) ps.setNull(3, Types.NVARCHAR); else ps.setString(3, formaPago);
            if (obs == null) ps.setNull(4, Types.NVARCHAR); else ps.setString(4, obs);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("sp_cxc_pagos_add sin resultado");
                int code = rs.getInt("code");
                String msg = rs.getString("msg");
                int pagoId = rs.getInt("pago_id");
                if (code != 0) throw new SQLException("sp_cxc_pagos_add ("+code+"): "+msg);
                return pagoId;
            }
        }
    }

    /** Aplica un pago a documentos (usa TVP tvp_cxc_aplicacion) → dbo.sp_cxc_pagos_aplicar */
    public void aplicarPago(int pagoId, List<CxcAplicacionItemDto> items) throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                // Tabla temporal para construir el TVP
                try (Statement st = c.createStatement()) {
                    st.execute(
                            "IF OBJECT_ID('tempdb..#tmp_apl') IS NOT NULL DROP TABLE #tmp_apl; " +
                                    "CREATE TABLE #tmp_apl (documento_id INT NOT NULL, monto_aplicado DECIMAL(19,4) NOT NULL);"
                    );
                }
                try (PreparedStatement psIns = c.prepareStatement(
                        "INSERT INTO #tmp_apl(documento_id, monto_aplicado) VALUES(?,?)")) {
                    for (var it : items) {
                        psIns.setInt(1, it.getDocumentoId());
                        psIns.setBigDecimal(2, it.getMonto());
                        psIns.addBatch();
                    }
                    psIns.executeBatch();
                }

                String exec =
                        "DECLARE @code INT, @msg NVARCHAR(200); " +
                                "DECLARE @tvp dbo.tvp_cxc_aplicacion; " +
                                "INSERT INTO @tvp(documento_id, monto_aplicado) SELECT documento_id, monto_aplicado FROM #tmp_apl; " +
                                "EXEC dbo.sp_cxc_pagos_aplicar @p_pago_id=?, @p_items=@tvp, " +
                                "  @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT; " +
                                "IF (@code IS NULL OR @code<>0) RAISERROR(@msg,16,1);";

                try (PreparedStatement ps = c.prepareStatement(exec)) {
                    ps.setInt(1, pagoId);
                    ps.execute();
                }

                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    /** Anula un pago (revierte aplicaciones y borra el pago) → dbo.sp_cxc_pagos_anular */
    public void anularPago(int pagoId, String motivo) throws SQLException {
        String sql =
                "DECLARE @code INT, @msg NVARCHAR(200); " +
                        "EXEC dbo.sp_cxc_pagos_anular @p_pago_id=?, @out_status_code=@code OUTPUT, @out_message=@msg OUTPUT; " +
                        "IF (@code IS NULL OR @code<>0) RAISERROR(@msg,16,1);";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, pagoId);
            ps.execute();
        }
    }

    /** Estado de cuenta simple → dbo.sp_cxc_estado_cuenta */
    public List<Map<String,Object>> estadoCuenta(int clienteId) throws SQLException {
        String sql = "EXEC dbo.sp_cxc_estado_cuenta @p_cliente_id=?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            List<Map<String,Object>> out = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("documentoId",      rs.getInt("documento_id"));
                    m.put("numeroDocumento",  rs.getString("numero_documento"));
                    m.put("fechaEmision",     rs.getDate("fecha_emision"));
                    m.put("fechaVencimiento", rs.getDate("fecha_vencimiento"));
                    m.put("montoTotal",       rs.getBigDecimal("monto_total"));
                    m.put("saldoPendiente",   rs.getBigDecimal("saldo_pendiente"));
                    m.put("estado",           rs.getString("estado"));
                    out.add(m);
                }
            }
            return out;
        }
    }
}
