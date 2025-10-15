package com.nexttechstore.nexttech_backend.dto.seg;

import java.time.OffsetDateTime;

public record UsuarioDto(
        Integer id,
        String nombreUsuario,
        String estado,              // 'A','I','B'
        OffsetDateTime ultimoAcceso,
        Integer intentosFallidos,
        Integer empleadoId,
        String empleadoNombreCompleto,
        Integer rolId,
        String rolNombre,
        OffsetDateTime fechaCreacion
) { }
