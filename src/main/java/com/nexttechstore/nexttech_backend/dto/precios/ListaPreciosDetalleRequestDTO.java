package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaPreciosDetalleRequestDTO {

    @NotNull(message = "El ID de la lista es requerido")
    private Integer listaId;

    @NotNull(message = "El ID del producto es requerido")
    private Integer productoId;

    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "La fecha desde es requerida")
    private LocalDate vigenteDesde;

    private LocalDate vigenteHasta;
}
