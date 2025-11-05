package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.model.compras.CompraPago;
import com.nexttechstore.nexttech_backend.model.compras.CompraPagoCrearRequest;
import com.nexttechstore.nexttech_backend.model.compras.CompraPagoEditarRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SPs reales (según tu script final):
 *  - sp_COMPRASPAGOS_Crear(@UsuarioId,@CompraId,@FormaPago,@Monto,@Referencia?, @PagoIdOut OUTPUT) -> SELECT fila creada
 *  - sp_COMPRASPAGOS_ListarPorCompra(@CompraId) -> SELECT ...
 *  - sp_COMPRASPAGOS_Editar(@UsuarioId,@Id,@FormaPago,@Monto,@Referencia?) -> SELECT fila editada
 *  - sp_COMPRASPAGOS_Anular(@UsuarioId,@Id) -> SELECT fila final (estado=X)
 *
 * Además, cuando compraId es null, hacemos SELECT directo sobre dbo.compras_pagos (estado<>'X')
 * y aplicamos texto LIKE en forma_pago/referencia.
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
        // Si hay compraId -> usar SP optimizado por compra
        if (compraId != null) {
            String sql = "EXEC dbo.sp_COMPRASPAGOS_ListarPorCompra @CompraId=?";
            List<CompraPago> list = jdbc.query(sql, ROW, compraId);
            // Filtro extra por texto (opcional) sobre el result set si vino
            if (texto != null && !texto.isBlank()) {
                final String t = texto.toLowerCase();
                List<CompraPago> filtered = new ArrayList<>();
                for (CompraPago p : list) {
                    String hay = (p.getReferencia() == null ? "" : p.getReferencia()).toLowerCase()
                            + "|" + (p.getForma_pago() == null ? "" : p.getForma_pago()).toLowerCase();
                    if (hay.contains(t)) filtered.add(p);
                }
                return filtered;
            }
            return list;
        }

        // Sin compraId -> SELECT directo (permite “select como listado” global)
        StringBuilder sb = new StringBuilder();
        List<Object> args = new ArrayList<>();

        sb.append("SELECT id, compra_id, forma_pago, monto, referencia ")
                .append("FROM dbo.compras_pagos ")
                .append("WHERE estado <> 'X' ");

        if (texto != null && !texto.isBlank()) {
            sb.append("AND (LOWER(forma_pago) LIKE ? OR LOWER(COALESCE(referencia,'')) LIKE ?) ");
            String like = "%" + texto.toLowerCase() + "%";
            args.add(like);
            args.add(like);
        }

        sb.append("ORDER BY fecha_pago DESC, id DESC");
        return jdbc.query(sb.toString(), ROW, args.toArray());
    }

    public CompraPago crear(Integer usuarioId, CompraPagoCrearRequest r) {
        // El SP hace SELECT de la fila creada; no necesitamos capturar OUTPUT local
        String sql = "EXEC dbo.sp_COMPRASPAGOS_Crear @UsuarioId=?, @CompraId=?, @FormaPago=?, @Monto=?, @Referencia=?, @PagoIdOut=NULL";
        return jdbc.queryForObject(sql, ROW,
                usuarioId,
                r.getCompra_id(),
                r.getForma_pago(),
                r.getMonto(),
                r.getReferencia()
        );
    }

    public CompraPago editar(Integer usuarioId, Integer id, CompraPagoEditarRequest r) {
        String sql = "EXEC dbo.sp_COMPRASPAGOS_Editar @UsuarioId=?, @Id=?, @FormaPago=?, @Monto=?, @Referencia=?";
        return jdbc.queryForObject(sql, ROW,
                usuarioId,
                id,
                r.getForma_pago(),
                r.getMonto(),
                r.getReferencia()
        );
    }

    public Integer eliminar(Integer usuarioId, Integer id) {
        // Anulamos (estado = 'X') y devolvemos el id afectado.
        String sql = "EXEC dbo.sp_COMPRASPAGOS_Anular @UsuarioId=?, @Id=?";
        try {
            return jdbc.queryForObject(sql, (rs, rn) -> rs.getInt("id"), usuarioId, id);
        } catch (EmptyResultDataAccessException e) {
            // Si el SP no devuelve fila (p.ej. ya estaba anulado), retornamos el id pedid
            return id;
        }
    }
}
