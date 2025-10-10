package com.nexttechstore.nexttech_backend.model.compras;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload para anular una compra con motivo.
 * SP esperado: dbo.sp_COMPRAS_Anular
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraAnularRequest {

    @NotNull
    private Integer usuarioId;

    @NotNull
    private Integer compraId;

    @NotBlank
    private String motivo;
}
