package com.nexttechstore.nexttech_backend.model.compras;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Entidad simple para fila de dbo.compras_pagos (snake_case).
 * Campos: id, compra_id, forma_pago, monto, referencia
 */
@Data
public class CompraPago {
    private Integer id;

    @NotNull(message = "compra_id es obligatorio.")
    private Integer compra_id;

    @NotBlank(message = "forma_pago es obligatoria.")
    private String forma_pago;

    @NotNull(message = "monto es obligatorio.")
    private BigDecimal monto;

    // Puede ser null según tu diseño
    private String referencia;
}
