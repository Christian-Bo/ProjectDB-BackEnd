package com.nexttechstore.nexttech_backend.model.compras;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Payload para sp_COMPRASPAGOS_Crear:
 */
@Data
public class CompraPagoCrearRequest {

    @NotNull(message = "compra_id es obligatorio.")
    private Integer compra_id;

    @NotBlank(message = "forma_pago es obligatoria.")
    private String forma_pago;

    @NotNull(message = "monto es obligatorio.")
    private BigDecimal monto;

    private String referencia;
}
