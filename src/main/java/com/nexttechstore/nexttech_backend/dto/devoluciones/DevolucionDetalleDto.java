package com.nexttechstore.nexttech_backend.dto.devoluciones;

import java.math.BigDecimal;

public record DevolucionDetalleDto(
        Integer id,              // id detalle_devoluciones_venta (si el SP lo expone)
        Integer detalleVentaId,
        Integer productoId,
        String  productoCodigo,
        String  productoNombre,
        BigDecimal cantidad,
        String  observaciones
) {}
