package com.nexttechstore.nexttech_backend.model.cxp;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Request para crear aplicaciones en lote: sp_CXP_Aplicaciones_Crear
 * @PagoId via path param; aquí enviamos la lista de items.
 */
@Data
public class CxpAplicacionesLoteRequest {

    @NotNull(message = "items es obligatorio.")
    private List<CxpAplicacionItemRequest> items;
}
