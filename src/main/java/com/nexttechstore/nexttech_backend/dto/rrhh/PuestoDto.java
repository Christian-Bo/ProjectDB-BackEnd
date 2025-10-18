package com.nexttechstore.nexttech_backend.dto.rrhh;

import java.time.OffsetDateTime;

public record PuestoDto(
        Integer id,
        String nombre,
        String descripcion,
        Boolean activo,
        Integer departamentoId,
        String departamentoNombre,
        OffsetDateTime fechaCreacion
) { }
