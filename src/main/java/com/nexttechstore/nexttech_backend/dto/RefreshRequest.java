package com.nexttechstore.nexttech_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank String token
) {}
