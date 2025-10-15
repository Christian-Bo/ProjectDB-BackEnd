package com.nexttechstore.nexttech_backend.dto.seg;

import jakarta.validation.constraints.*;

public record UsuarioCreateRequest(
        @NotBlank String nombreUsuario,
        @NotBlank String password,
        @NotNull  Integer empleadoId,
        @NotNull  Integer rolId,
        @Pattern(regexp = "A|I|B", message = "estado debe ser A, I o B")
        String estado
) { }
