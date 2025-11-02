package com.nexttechstore.nexttech_backend.dto.devoluciones;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record DevolucionCreateRequestDto(
        @NotNull Integer ventaId,
        @NotNull LocalDate fecha,
        @Size(max = 200) String motivo,
        @NotNull List<Item> items
) {
    public record Item(
            @NotNull Integer detalleVentaId,
            @NotNull Integer productoId,
            @NotNull Integer cantidad,
            @Size(max = 200) String observaciones
    ) {}
}
