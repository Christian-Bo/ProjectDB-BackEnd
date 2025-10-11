package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.model.cxp.CxpPago;
import com.nexttechstore.nexttech_backend.model.cxp.CxpPagoEditarRequest;
import com.nexttechstore.nexttech_backend.model.cxp.CxpPagoRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SPs reales:
 *  - sp_CXP_Pagos_Crear(@UsuarioId,@ProveedorId,@FechaPago,@FormaPago,@MontoTotal,@Observaciones?,@PagoIdOut OUTPUT) -> SELECT pago + SELECT aplicaciones
 *  - sp_CXP_Pagos_Listar(@ProveedorId?,@Texto?) -> SELECT ...
 *  - sp_CXP_Pagos_Editar(@UsuarioId,@Id,@FechaPago,@FormaPago,@MontoTotal,@Observaciones?) -> SELECT pago editado
 *  - sp_CXP_Pagos_Eliminar(@UsuarioId,@Id) -> SELECT eliminado,id
 *  - sp_CXP_Pagos_Anular(@UsuarioId,@Id) -> SELECT anulado,id
 */
@Repository
public class CxpPagosSpRepository {

    private final JdbcTemplate jdbc;

    public CxpPagosSpRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<CxpPago> ROW = new RowMapper<>() {
        @Override public CxpPago mapRow(ResultSet rs, int rowNum) throws SQLException {
            CxpPago p = new CxpPago();
            p.setId(rs.getInt("id"));
            p.setProveedor_id(rs.getInt("proveedor_id"));
            p.setFecha_pago(rs.getDate("fecha_pago").toLocalDate());
            p.setForma_pago(rs.getString("forma_pago"));
            p.setMonto_total(rs.getBigDecimal("monto_total"));
            p.setObservaciones(rs.getString("observaciones"));
            return p;
        }
    };

    public List<CxpPago> listar(Integer proveedorId, String texto) {
        String sql = "EXEC dbo.sp_CXP_Pagos_Listar @ProveedorId=?, @Texto=?";
        return jdbc.query(sql, ROW, proveedorId, texto);
    }

    public CxpPago crear(Integer usuarioId, CxpPagoRequest r) {
        // El SP retorna el pago (y además un SELECT de aplicaciones). queryForObject toma el primer result set.
        String sql = "EXEC dbo.sp_CXP_Pagos_Crear "
                + "@UsuarioId=?, @ProveedorId=?, @FechaPago=?, @FormaPago=?, @MontoTotal=?, @Observaciones=?, @PagoIdOut=NULL";
        return jdbc.queryForObject(sql, ROW,
                usuarioId, r.getProveedor_id(), r.getFecha_pago(), r.getForma_pago(), r.getMonto_total(), r.getObservaciones());
    }

    public CxpPago editar(Integer usuarioId, Integer id, CxpPagoEditarRequest r) {
        String sql = "EXEC dbo.sp_CXP_Pagos_Editar "
                + "@UsuarioId=?, @Id=?, @FechaPago=?, @FormaPago=?, @MontoTotal=?, @Observaciones=?";
        return jdbc.queryForObject(sql, ROW,
                usuarioId, id, r.getFecha_pago(), r.getForma_pago(), r.getMonto_total(), r.getObservaciones());
    }

    public Integer eliminar(Integer usuarioId, Integer id) {
        return jdbc.queryForObject(
                "EXEC dbo.sp_CXP_Pagos_Eliminar @UsuarioId=?, @Id=?",
                (rs, rn) -> rs.getInt("id"),
                usuarioId, id
        );
    }

    public Integer anular(Integer usuarioId, Integer id) {
        return jdbc.queryForObject(
                "EXEC dbo.sp_CXP_Pagos_Anular @UsuarioId=?, @Id=?",
                (rs, rn) -> rs.getInt("id"),
                usuarioId, id
        );
    }
}
