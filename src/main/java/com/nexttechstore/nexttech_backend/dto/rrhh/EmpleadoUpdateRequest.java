package com.nexttechstore.nexttech_backend.dto.rrhh;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EmpleadoUpdateRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank @Size(min=6, max=13) String dpi,
        String nit,
        String telefono,
        @Size(min=6) String email,
        String direccion,
        LocalDate fechaNacimiento,
        @NotNull LocalDate fechaIngreso,
        LocalDate fechaSalida,
        @NotBlank String estado,   // 'A','I','S'
        @NotNull Integer puestoId,
        Integer jefeInmediatoId,
        String foto
) { }
