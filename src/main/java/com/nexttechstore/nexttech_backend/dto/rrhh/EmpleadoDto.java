package com.nexttechstore.nexttech_backend.dto.rrhh;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record EmpleadoDto(
        Integer id,
        String codigo,
        String nombres,
        String apellidos,
        String dpi,
        String nit,
        String telefono,
        String email,
        String direccion,
        LocalDate fechaNacimiento,
        LocalDate fechaIngreso,
        LocalDate fechaSalida,
        String estado,
        Integer puestoId,
        String puestoNombre,
        Integer departamentoId,
        String departamentoNombre,
        Integer jefeId,
        String jefeNombreCompleto,
        String foto,
        OffsetDateTime fechaCreacion
) { }
