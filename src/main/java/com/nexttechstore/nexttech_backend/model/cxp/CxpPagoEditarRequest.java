package com.nexttechstore.nexttech_backend.model.cxp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload para sp_CXP_Pagos_Editar:
 *   @UsuarioId, @Id, @FechaPago, @FormaPago, @MontoTotal, @Observaciones?
 */
@Data
public class CxpPagoEditarRequest {

    @NotNull(message = "fecha_pago es obligatoria.")
    private LocalDate fecha_pago;

    @NotBlank(message = "forma_pago es obligatoria.")
    private String forma_pago;

    @NotNull(message = "monto_total es obligatorio.")
    private BigDecimal monto_total;

    private String observaciones;
}
