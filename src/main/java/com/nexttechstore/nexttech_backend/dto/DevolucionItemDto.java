package com.nexttechstore.nexttech_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DevolucionItemDto {

    @NotNull
    private Integer detalleVentaId;   // debe existir en detalle_ventas.id

    @NotNull
    private Integer productoId;

    @NotNull @Min(1)
    private Integer cantidad;

    // opcional en el TVP
    private String observaciones;     // puede ser null
}
