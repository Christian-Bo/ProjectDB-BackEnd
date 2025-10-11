package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.PagoRequestDto;

public interface CxcService {
    int aplicarPago(PagoRequestDto req);
}
