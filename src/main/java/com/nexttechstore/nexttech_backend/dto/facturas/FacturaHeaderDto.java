package com.nexttechstore.nexttech_backend.dto.facturas;

import java.math.BigDecimal;

public record FacturaHeaderDto(
        Integer id,
        Integer serieId,
        String serie,
        String correlativo,          // <-- AHORA String
        String numero,               // si 'numero' puede ser varchar en tu BD, déjalo String
        String fechaEmision,
        boolean activa,
        Integer ventaId,
        BigDecimal subtotal,
        BigDecimal descuentoGeneral,
        BigDecimal iva,
        BigDecimal total,
        String cliente,
        String nit,
        String tipoPago
) {}
