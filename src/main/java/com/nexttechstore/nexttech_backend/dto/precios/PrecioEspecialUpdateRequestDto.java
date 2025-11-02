package com.nexttechstore.nexttech_backend.dto.precios;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PrecioEspecialUpdateRequestDto(
        Integer clienteId,
        Integer productoId,
        BigDecimal precioEspecial,        // nullable
        BigDecimal descuentoPorcentaje,   // nullable (0..100)
        LocalDate fechaInicio,            // nullable
        LocalDate fechaVencimiento,       // nullable
        Boolean activo                    // required para update
) {}
