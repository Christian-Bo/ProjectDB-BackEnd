package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaMargenResponseDTO {
    private Integer id;
    private Integer categoriaId;
    private String categoriaNombre;
    private Integer marcaId;
    private String marcaNombre;
    private BigDecimal margenPct;
    private String tipoRegla;
    private Integer prioridad;
}