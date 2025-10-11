package com.nexttechstore.nexttech_backend.model.cxp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload para sp_CXP_Pagos_Crear:
 *   @UsuarioId, @ProveedorId, @FechaPago, @FormaPago, @MontoTotal, @Observaciones?, @PagoIdOut OUTPUT
 */
@Data
public class CxpPagoRequest {

    @NotNull(message = "proveedor_id es obligatorio.")
    private Integer proveedor_id;

    @NotNull(message = "fecha_pago es obligatoria.")
    private LocalDate fecha_pago;

    @NotBlank(message = "forma_pago es obligatoria.")
    private String forma_pago;

    @NotNull(message = "monto_total es obligatorio.")
    private BigDecimal monto_total;

    private String observaciones;
}
