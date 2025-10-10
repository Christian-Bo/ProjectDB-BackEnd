package com.nexttechstore.nexttech_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VentaRequestDto {

    @NotNull @Min(1)
    private Integer usuarioId;

    @NotNull @Min(1)
    private Integer clienteId;

    // ⚠️Compat: lo dejamos pero ya no lo usaremos; preferir bodegaOrigenId
    private Integer serieId;

    // NUEVOS (opcionales)
    private Integer vendedorId;      // si no viene, usamos usuarioId
    private Integer cajeroId;        // si no viene, puede quedar null o usar usuarioId si quieres
    private Integer bodegaOrigenId;  // si no viene, usamos serieId como fallback
    private String  tipoPago;        // 'C' contado, 'R' crédito; default 'C'
    private String  observaciones;   // default ""

    @NotEmpty
    private List<VentaItemDto> items;
}
