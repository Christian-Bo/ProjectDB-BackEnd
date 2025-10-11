package com.nexttechstore.nexttech_backend.model.compras;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa UNA línea para creación o agregado de detalle.
 * Se envía como TVP (tabla valor parámetro) al SP.
 * Ajusta el TVP en el repositorio si tu tipo difiere (dbo.tvp_DetalleCompra).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraDetalleRequest {

    @NotNull
    private Integer productoId;

    @NotNull
    @Positive
    private Integer cantidadPedida;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal descuentoLinea;

    private String lote;
    private LocalDate fechaVencimiento;
}
