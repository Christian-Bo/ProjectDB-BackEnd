package com.nexttechstore.nexttech_backend.dto.rrhh;

public record EmpleadoFilter(
        Integer departamentoId,
        Integer puestoId,
        String estado,      // 'A','I','S'
        String q            // búsqueda por nombre/apellido
) { }
