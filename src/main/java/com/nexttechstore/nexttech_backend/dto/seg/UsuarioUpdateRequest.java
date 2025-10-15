package com.nexttechstore.nexttech_backend.dto.seg;

import jakarta.validation.constraints.*;

public record UsuarioUpdateRequest(
        @NotBlank String nombreUsuario,
        String password,                 // opcional (si viene, se re-hash)
        @NotNull Integer empleadoId,
        @NotNull Integer rolId,
        @Pattern(regexp = "A|I|B", message = "estado debe ser A, I o B")
        String estado,
        @Min(0) Integer intentosFallidos // opcional
) { }
