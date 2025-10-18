package com.nexttechstore.nexttech_backend.dto;

import java.time.OffsetDateTime;

public record LoginResponse(
        String token,
        OffsetDateTime expiresAt,
        UserDto user
) {}
