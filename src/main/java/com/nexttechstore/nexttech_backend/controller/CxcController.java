package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.PagoRequestDto;
import com.nexttechstore.nexttech_backend.security.AllowedRoles;
import com.nexttechstore.nexttech_backend.service.api.CxcService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllowedRoles({"FINANZAS"})
@RestController
@RequestMapping("/api/cxc")
public class CxcController {

    private final CxcService service;

    public CxcController(CxcService service) {
        this.service = service;
    }

    @PostMapping("/pagos")
    public Map<String, Object> aplicarPago(@Valid @RequestBody PagoRequestDto req) {
        int pagoId = service.aplicarPago(req);
        return Map.of("pagoId", pagoId, "status", "OK");
    }
}
