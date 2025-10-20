package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaPreciosIncrementoRequestDTO {

    @NotNull(message = "El ID de la lista es requerido")
    private Integer listaId;

    @NotNull(message = "El porcentaje de incremento es requerido")
    private BigDecimal porcentajeIncremento;

    @NotNull(message = "La fecha de vigencia es requerida")
    private LocalDate vigenteDesde;

    private Integer categoriaId; // Filtro opcional

    private Integer marcaId; // Filtro opcional
}