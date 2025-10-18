package com.nexttechstore.nexttech_backend.dto;

public record UserDto(
        Integer id,
        String nombreUsuario,
        String estado,
        Integer empleadoId,
        Integer rolId,
        String rolNombre
) {}
