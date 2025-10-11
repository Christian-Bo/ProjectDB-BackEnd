package com.nexttechstore.nexttech_backend.model.cxp;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Item para TVP dbo.tvp_AplicacionesPago: (documento_id, monto_aplicado)
 */
@Data
public class CxpAplicacionItemRequest {

    @NotNull(message = "documento_id es obligatorio.")
    private Integer documento_id;

    @NotNull(message = "monto_aplicado es obligatorio.")
    private BigDecimal monto_aplicado;
}
