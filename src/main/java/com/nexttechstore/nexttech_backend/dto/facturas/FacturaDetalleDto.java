package com.nexttechstore.nexttech_backend.dto.facturas;

import java.math.BigDecimal;

public record FacturaDetalleDto(
        Integer productoId,
        String producto,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal descuentoLinea,
        BigDecimal subtotal,
        String lote,
        String fechaVencimiento
) {}
