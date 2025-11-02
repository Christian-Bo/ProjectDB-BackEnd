package com.nexttechstore.nexttech_backend.dto.devoluciones;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DevolucionHeaderDto(
        Integer id,
        String  numeroDevolucion,
        Integer ventaId,
        LocalDate fechaDevolucion,
        String  motivo,
        String  estado,
        LocalDateTime fechaCreacion,
        List<DevolucionDetalleDto> items
) {}
