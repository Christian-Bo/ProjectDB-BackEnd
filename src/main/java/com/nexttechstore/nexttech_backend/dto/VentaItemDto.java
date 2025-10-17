package com.nexttechstore.nexttech_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VentaItemDto {

    @NotNull
    private Integer productoId;

    // Ya NO requerido al crear (lo dejamos por compatibilidad pero sin @NotNull)
    private Integer bodegaId;

    @NotNull
    private BigDecimal cantidad;       // OJO: tu TVP v2 espera INT; convertimos en el repo

    @NotNull
    private BigDecimal precioUnitario;

    // Opcionales
    private BigDecimal descuento;        // por línea
    private BigDecimal impuesto;         // si lo usas después
    private String lote;                 // NUEVO: se guarda en detalle_ventas.lote
    private LocalDate fechaVencimiento;  // NUEVO: se guarda en detalle_ventas.fecha_vencimiento
}
