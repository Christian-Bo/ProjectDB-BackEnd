package com.nexttechstore.nexttech_backend.dto.devoluciones;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DevolucionListItemDto(
        Integer id,
        String  numeroDevolucion,
        Integer ventaId,
        LocalDate fechaDevolucion,
        String  motivo,
        String  estado,
        BigDecimal totalDevuelto // si tu SP lo devuelve; si no, puedes quitarlo
) {}
