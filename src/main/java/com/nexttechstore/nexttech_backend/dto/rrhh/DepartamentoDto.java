package com.nexttechstore.nexttech_backend.dto.rrhh;
import java.time.OffsetDateTime;

public record DepartamentoDto(
        Integer id,
        String nombre,
        String descripcion,
        Boolean activo,
        OffsetDateTime fechaCreacion
) { }
