package com.nexttechstore.nexttech_backend.dto.clientes;

import java.math.BigDecimal;

public record ClienteCreateRequestDto(
        String codigo,                // opcional (si null, lo genera el SP)
        String nombre,                // requerido
        String nit,                   // opcional
        String telefono,              // opcional
        String direccion,             // opcional
        String email,                 // opcional
        BigDecimal limiteCredito,     // opcional (null => 0)
        Integer diasCredito,          // opcional (null => 0)
        String estado,                // opcional ('A' por defecto en SP)
        String tipoCliente,           // opcional ('I' por defecto en SP)
        Integer registradoPor         // opcional
) {}
