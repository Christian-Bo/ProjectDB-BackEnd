package com.nexttechstore.nexttech_backend.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequest {

    @Min(value = 1, message = "La página debe ser mayor o igual a 1")
    private Integer page = 1;

    @Min(value = 1, message = "El tamaño de página debe ser mayor o igual a 1")
    private Integer pageSize = 20;

    private String sortBy;

    @Pattern(regexp = "ASC|DESC", message = "La dirección de ordenamiento debe ser ASC o DESC")
    private String sortDir = "ASC";

    private String q; // Búsqueda general
}