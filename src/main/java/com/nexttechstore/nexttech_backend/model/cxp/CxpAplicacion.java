package com.nexttechstore.nexttech_backend.model.cxp;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad para dbo.cxp_aplicaciones (snake_case)
 */
@Data
public class CxpAplicacion {
    private Integer id;

    @NotNull(message = "pago_id es obligatorio.")
    private Integer pago_id;

    @NotNull(message = "documento_id es obligatorio.")
    private Integer documento_id;

    @NotNull(message = "monto_aplicado es obligatorio.")
    private java.math.BigDecimal monto_aplicado;

    private LocalDate fecha_aplicacion;
}
