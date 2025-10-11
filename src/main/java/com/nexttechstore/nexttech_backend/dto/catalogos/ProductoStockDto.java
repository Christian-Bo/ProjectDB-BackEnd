package com.nexttechstore.nexttech_backend.dto.catalogos;

import java.math.BigDecimal;

public record ProductoStockDto(
        Integer id,
        String codigo,
        String nombre,
        BigDecimal precioVenta,
        Integer stockDisponible
) {}
