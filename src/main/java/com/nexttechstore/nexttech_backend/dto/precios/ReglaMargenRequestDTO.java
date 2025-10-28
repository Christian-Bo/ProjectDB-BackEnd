package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaMargenRequestDTO {

    private Integer categoriaId; // Puede ser null

    private Integer marcaId; // Puede ser null

    @NotNull(message = "El porcentaje de margen es requerido")
    @DecimalMin(value = "0.0", message = "El margen no puede ser negativo")
    @DecimalMax(value = "1000.0", message = "El margen no puede exceder 1000%")
    private BigDecimal margenPct;
}
