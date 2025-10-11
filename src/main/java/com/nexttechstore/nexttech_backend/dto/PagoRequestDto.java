package com.nexttechstore.nexttech_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoRequestDto {

    @NotNull(message = "usuarioId es obligatorio")
    private Integer usuarioId;

    @NotNull(message = "documentoId es obligatorio")
    private Integer documentoId; // factura/venta

    @DecimalMin(value = "0.01", message = "monto debe ser > 0")
    private Double monto;
}
