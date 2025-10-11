package com.nexttechstore.nexttech_backend.model.cxp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad para dbo.cxp_pagos (snake_case)
 */
@Data
public class CxpPago {
    private Integer id;

    @NotNull(message = "proveedor_id es obligatorio.")
    private Integer proveedor_id;

    @NotNull(message = "fecha_pago es obligatoria.")
    private LocalDate fecha_pago;

    @NotBlank(message = "forma_pago es obligatoria.")
    private String forma_pago;

    @NotNull(message = "monto_total es obligatorio.")
    private BigDecimal monto_total;

    private String observaciones;

    private java.time.OffsetDateTime fecha_creacion; // audit
}
