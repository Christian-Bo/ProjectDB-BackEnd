package com.nexttechstore.nexttech_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VentaItemDto {

    @NotNull
    private Integer productoId;

    @NotNull
    private Integer bodegaId;

    @NotNull
    private BigDecimal cantidad;

    @NotNull
    private BigDecimal precioUnitario;

    // Opcionales
    private BigDecimal descuento;        // por línea
    private BigDecimal impuesto;         // por línea (si lo usas después)
    private String lote;                 // NUEVO: para no devolver null
    private LocalDate fechaVencimiento;  // NUEVO: si aplica (productos con vencimiento)
}
