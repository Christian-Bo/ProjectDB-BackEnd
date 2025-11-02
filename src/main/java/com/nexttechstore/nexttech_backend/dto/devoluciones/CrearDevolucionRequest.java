package com.nexttechstore.nexttech_backend.dto.devoluciones;

import java.time.LocalDate;
import java.util.List;

public record CrearDevolucionRequest(
        Integer ventaId,
        LocalDate fecha,
        String motivo,
        List<Item> items
) {
    public record Item(
            Integer detalleVentaId,
            Integer productoId,
            Integer cantidad,
            String  observaciones
    ) {}
}
