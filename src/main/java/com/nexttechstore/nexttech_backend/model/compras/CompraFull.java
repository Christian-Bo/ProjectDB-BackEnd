package com.nexttechstore.nexttech_backend.model.compras;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Respuesta compuesta: cabecera + detalle completo.
 * Útil para GET /api/compras/{id}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraFull {
    private CompraCabecera cabecera;
    private List<CompraDetalle> detalle;
}
