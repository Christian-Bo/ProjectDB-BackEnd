package com.nexttechstore.nexttech_backend.dto.facturas;

import java.math.BigDecimal;

public record FacturaHeaderDto(
        Integer id,
        Integer serieId,
        String serie,
        String correlativo,
        String numero,
        String fechaEmision,
        boolean activa,
        Integer ventaId,
        BigDecimal subtotal,
        BigDecimal descuentoGeneral,
        BigDecimal iva,
        BigDecimal total,
        String cliente,
        String nit,
        String tipoPago,
        // NUEVOS
        BigDecimal descuentoLineas,
        BigDecimal descuentoTotal
) {}
