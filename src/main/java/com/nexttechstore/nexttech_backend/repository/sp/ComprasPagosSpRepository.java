package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.model.compras.CompraPago;
import com.nexttechstore.nexttech_backend.model.compras.CompraPagoCrearRequest;
import com.nexttechstore.nexttech_backend.model.compras.CompraPagoEditarRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SPs reales (según tu script):
 *  - sp_COMPRASPAGOS_Crear(@UsuarioId,@CompraId,@FormaPago,@Monto,@Referencia?, @PagoIdOut OUTPUT) -> SELECT fila creada
 *  - sp_COMPRASPAGOS_Listar(@CompraId?,@Texto?) -> SELECT ...
 *  - sp_COMPRASPAGOS_Editar(@UsuarioId,@Id,@FormaPago,@Monto,@Referencia?) -> SELECT fila editada
 *  - sp_COMPRASPAGOS_Eliminar(@UsuarioId,@Id) -> SELECT eliminado,id
 */
@Repository
public class ComprasPagosSpRepository {

    private final JdbcTemplate jdbc;

    public ComprasPagosSpRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<CompraPago> ROW = new RowMapper<>() {
        @Override public CompraPago mapRow(ResultSet rs, int rowNum) throws SQLException {
            CompraPago x = new CompraPago();
            x.setId(rs.getInt("id"));
            x.setCompra_id(rs.getInt("compra_id"));
            x.setForma_pago(rs.getString("forma_pago"));
            x.setMonto(rs.getBigDecimal("monto"));
            x.setReferencia(rs.getString("referencia"));
            return x;
        }
    };

    public List<CompraPago> listar(Integer compraId, String texto) {
        String sql = "EXEC dbo.sp_COMPRASPAGOS_Listar @CompraId=?, @Texto=?";
        return jdbc.query(sql, ROW, compraId, texto);
    }

    public CompraPago crear(Integer usuarioId, CompraPagoCrearRequest r) {
        String sql = ""
                + "EXEC dbo.sp_COMPRASPAGOS_Crear "
                + "  @UsuarioId=?, @CompraId=?, @FormaPago=?, @Monto=?, @Referencia=?, @PagoIdOut=?;";
        // El SP hace SELECT de la fila creada; usamos la variante sin OUTPUT local:
        sql = "EXEC dbo.sp_COMPRASPAGOS_Crear @UsuarioId=?, @CompraId=?, @FormaPago=?, @Monto=?, @Referencia=?, @PagoIdOut=NULL";
        return jdbc.queryForObject(sql, ROW, usuarioId, r.getCompra_id(), r.getForma_pago(), r.getMonto(), r.getReferencia());
    }

    public CompraPago editar(Integer usuarioId, Integer id, CompraPagoEditarRequest r) {
        String sql = "EXEC dbo.sp_COMPRASPAGOS_Editar @UsuarioId=?, @Id=?, @FormaPago=?, @Monto=?, @Referencia=?";
        return jdbc.queryForObject(sql, ROW, usuarioId, id, r.getForma_pago(), r.getMonto(), r.getReferencia());
    }

    public Integer eliminar(Integer usuarioId, Integer id) {
        // Retorna SELECT eliminado, id -> devolvemos id
        return jdbc.queryForObject(
                "EXEC dbo.sp_COMPRASPAGOS_Eliminar @UsuarioId=?, @Id=?",
                (rs, rn) -> rs.getInt("id"),
                usuarioId, id
        );
    }
}
