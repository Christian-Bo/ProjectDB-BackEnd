package com.nexttechstore.nexttech_backend.dto.catalogos;

public record DetalleVentaLiteDto(
        Integer id,            // id detalle_ventas
        Integer productoId,
        String  productoNombre,
        Integer cantidad       // cantidad vendida
) {}
