package com.nexttechstore.nexttech_backend.dto.clientes;

import java.math.BigDecimal;

public record ClienteUpdateRequestDto(
        String codigo,                // requerido por el SP de update
        String nombre,                // requerido por el SP de update
        String nit,                   // opcional
        String telefono,              // opcional
        String direccion,             // opcional
        String email,                 // opcional
        BigDecimal limiteCredito,     // opcional (null => conserva)
        Integer diasCredito,          // opcional (null => conserva)
        String estado,                // opcional (null => conserva)
        String tipoCliente            // opcional (null => conserva)
) {}
