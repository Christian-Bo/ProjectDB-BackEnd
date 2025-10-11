package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.model.cxp.CxpDocumento;
import com.nexttechstore.nexttech_backend.model.cxp.CxpDocumentoEditarRequest;
import com.nexttechstore.nexttech_backend.model.cxp.CxpDocumentoRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SPs reales:
 *  - sp_CXP_Documentos_Crear(@UsuarioId,@ProveedorId,@OrigenTipo,@OrigenId,@NumeroDocumento,@FechaEmision,@FechaVencimiento?,@Moneda?,@MontoTotal,@DocumentoIdOut OUTPUT) -> SELECT fila creada
 *  - sp_CXP_Documentos_Listar(@ProveedorId?,@Texto?) -> SELECT ...
 *  - sp_CXP_Documentos_Editar(@UsuarioId,@Id,@NumeroDocumento,@FechaEmision,@FechaVencimiento?,@Moneda,@MontoTotal) -> SELECT fila editada
 *  - sp_CXP_Documentos_Anular(@UsuarioId,@Id) -> SELECT anulado,id
 */
@Repository
public class CxpDocumentosSpRepository {

    private final JdbcTemplate jdbc;

    public CxpDocumentosSpRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<CxpDocumento> ROW = new RowMapper<>() {
        @Override public CxpDocumento mapRow(ResultSet rs, int rowNum) throws SQLException {
            CxpDocumento d = new CxpDocumento();
            d.setId(rs.getInt("id"));
            d.setProveedor_id(rs.getInt("proveedor_id"));
            d.setOrigen_tipo(rs.getString("origen_tipo"));
            d.setOrigen_id(rs.getInt("origen_id"));
            d.setNumero_documento(rs.getString("numero_documento"));
            d.setFecha_emision(rs.getDate("fecha_emision").toLocalDate());
            if (rs.getDate("fecha_vencimiento") != null) {
                d.setFecha_vencimiento(rs.getDate("fecha_vencimiento").toLocalDate());
            }
            d.setMoneda(rs.getString("moneda"));
            d.setMonto_total(rs.getBigDecimal("monto_total"));
            d.setSaldo_pendiente(rs.getBigDecimal("saldo_pendiente"));
            d.setEstado(rs.getString("estado"));
            return d;
        }
    };

    public List<CxpDocumento> listar(Integer proveedorId, String texto) {
        String sql = "EXEC dbo.sp_CXP_Documentos_Listar @ProveedorId=?, @Texto=?";
        return jdbc.query(sql, ROW, proveedorId, texto);
    }

    public CxpDocumento crear(Integer usuarioId, CxpDocumentoRequest r) {
        String sql = "EXEC dbo.sp_CXP_Documentos_Crear "
                + "@UsuarioId=?, @ProveedorId=?, @OrigenTipo=?, @OrigenId=?, "
                + "@NumeroDocumento=?, @FechaEmision=?, @FechaVencimiento=?, "
                + "@Moneda=?, @MontoTotal=?, @DocumentoIdOut=NULL";
        return jdbc.queryForObject(sql, ROW,
                usuarioId, r.getProveedor_id(), r.getOrigen_tipo(), r.getOrigen_id(),
                r.getNumero_documento(), r.getFecha_emision(), r.getFecha_vencimiento(),
                r.getMoneda(), r.getMonto_total());
    }

    public CxpDocumento editar(Integer usuarioId, Integer id, CxpDocumentoEditarRequest r) {
        String sql = "EXEC dbo.sp_CXP_Documentos_Editar "
                + "@UsuarioId=?, @Id=?, @NumeroDocumento=?, @FechaEmision=?, @FechaVencimiento=?, @Moneda=?, @MontoTotal=?";
        return jdbc.queryForObject(sql, ROW,
                usuarioId, id, r.getNumero_documento(), r.getFecha_emision(), r.getFecha_vencimiento(),
                r.getMoneda(), r.getMonto_total());
    }

    public Integer anular(Integer usuarioId, Integer id) {
        return jdbc.queryForObject(
                "EXEC dbo.sp_CXP_Documentos_Anular @UsuarioId=?, @Id=?",
                (rs, rn) -> rs.getInt("id"),
                usuarioId, id
        );
    }
}
