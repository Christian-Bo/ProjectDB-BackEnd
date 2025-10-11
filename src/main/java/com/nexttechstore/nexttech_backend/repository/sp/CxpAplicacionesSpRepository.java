package com.nexttechstore.nexttech_backend.repository.sp;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.nexttechstore.nexttech_backend.model.cxp.CxpAplicacion;
import com.nexttechstore.nexttech_backend.model.cxp.CxpAplicacionItemRequest;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SPs reales:
 *  - sp_CXP_Aplicaciones_Crear(@UsuarioId,@PagoId,@Aplicaciones dbo.tvp_AplicacionesPago READONLY) -> SELECT aplicaciones del pago
 *  - sp_CXP_Aplicaciones_Listar(@PagoId) -> SELECT ...
 * (La recalculación de saldos la hace el propio SP)
 */
@Repository
public class CxpAplicacionesSpRepository {

    private final JdbcTemplate jdbc;

    public CxpAplicacionesSpRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<CxpAplicacion> ROW = new RowMapper<>() {
        @Override
        public CxpAplicacion mapRow(ResultSet rs, int rowNum) throws SQLException {
            CxpAplicacion a = new CxpAplicacion();
            a.setId(rs.getInt("id"));
            a.setPago_id(rs.getInt("pago_id"));
            a.setDocumento_id(rs.getInt("documento_id"));
            a.setMonto_aplicado(rs.getBigDecimal("monto_aplicado"));
            java.sql.Date f = rs.getDate("fecha_aplicacion");
            if (f != null) a.setFecha_aplicacion(f.toLocalDate());
            return a;
        }
    };

    /** Listar aplicaciones por pago. */
    public List<CxpAplicacion> listar(Integer pagoId) {
        return jdbc.query("EXEC dbo.sp_CXP_Aplicaciones_Listar @PagoId=?", ROW, pagoId);
    }

    /**
     * Crear aplicaciones en lote usando TVP dbo.tvp_AplicacionesPago(documento_id INT, monto_aplicado DECIMAL(19,4)).
     * Se captura la SQLServerException (checked) para evitar propagarla por las capas superiores.
     */
    public List<CxpAplicacion> crearLote(Integer usuarioId, Integer pagoId, List<CxpAplicacionItemRequest> items) {
        try {
            // Construir TVP en memoria
            SQLServerDataTable tvp = new SQLServerDataTable();
            tvp.addColumnMetadata("documento_id", java.sql.Types.INTEGER);
            tvp.addColumnMetadata("monto_aplicado", java.sql.Types.DECIMAL);
            for (CxpAplicacionItemRequest it : items) {
                tvp.addRow(it.getDocumento_id(), it.getMonto_aplicado());
            }

            // Ejecutar SP que recibe el TVP
            String sql = "EXEC dbo.sp_CXP_Aplicaciones_Crear @UsuarioId=?, @PagoId=?, @Aplicaciones=?";
            return jdbc.query(sql, ROW, usuarioId, pagoId, tvp);

        } catch (SQLServerException e) {
            // Envuelve la checked exception en una RuntimeException de Spring para no cambiar firmas
            throw new DataRetrievalFailureException("Error construyendo/enviando TVP dbo.tvp_AplicacionesPago", e);
        }
    }
}
