package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.PagoRequestDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class CxcSpRepository {

    private final DataSource dataSource;

    public CxcSpRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Ejecuta: EXEC dbo.sp_cxc_aplicar_pago @UsuarioId, @DocumentoId, @Monto, OUTPUTs
     * Retorna el OutPagoId del pago registrado.
     */
    public int aplicarPago(PagoRequestDto req) throws SQLException {
        String sql =
                "DECLARE @OutPagoId INT; " +
                        "EXEC dbo.sp_cxc_aplicar_pago " +
                        "  @UsuarioId=?, @DocumentoId=?, @Monto=?, @OutPagoId=@OutPagoId OUTPUT; " +
                        "SELECT @OutPagoId AS OutPagoId;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, req.getUsuarioId());
            ps.setInt(2, req.getDocumentoId());
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(req.getMonto()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int outId = rs.getInt("OutPagoId");
                    if (outId <= 0) throw new SQLException("Fallo al aplicar pago");
                    return outId;
                } else {
                    throw new SQLException("Sin resultado de sp_cxc_aplicar_pago");
                }
            }
        }
    }
}
