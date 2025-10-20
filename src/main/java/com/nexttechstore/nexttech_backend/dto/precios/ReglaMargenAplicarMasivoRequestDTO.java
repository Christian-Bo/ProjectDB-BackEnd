package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaMargenAplicarMasivoRequestDTO {
    private Integer categoriaId;
    private Integer marcaId;
    private Boolean soloSinPrecioVenta = false;
}