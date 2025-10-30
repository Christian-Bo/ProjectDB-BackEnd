package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.cxc.CxcAplicacionItemDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CxcService {
    int crearPago(Integer clienteId, BigDecimal monto, String formaPago, LocalDate fecha, String observaciones);
    void aplicarPago(Integer pagoId, List<CxcAplicacionItemDto> items);
    void anularPago(Integer pagoId, String motivo);
    List<Map<String,Object>> estadoCuenta(Integer clienteId);
}
