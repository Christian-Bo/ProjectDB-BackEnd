package com.nexttechstore.nexttech_backend.model.compras;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload para editar UNA línea del detalle.
 * SP esperado: dbo.sp_COMPRAS_EditarDetalleLinea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraEditarDetalleRequest {

    @NotNull
    private Integer usuarioId;

    @NotNull
    private Integer detalleId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal descuentoLinea;

    @Positive
    private Integer cantidadPedida;

    private String lote;
    private LocalDate fechaVencimiento;
}
