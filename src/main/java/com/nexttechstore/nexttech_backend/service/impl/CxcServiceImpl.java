package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.cxc.CxcAplicacionItemDto;
import com.nexttechstore.nexttech_backend.repository.sp.CxcSpRepository;
import com.nexttechstore.nexttech_backend.service.api.CxcService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class CxcServiceImpl implements CxcService {
    private final CxcSpRepository repo;
    public CxcServiceImpl(CxcSpRepository repo){ this.repo = repo; }

    @Override
    public int crearPago(Integer clienteId, BigDecimal monto, String formaPago, LocalDate fecha, String obs) {
        try {
            return repo.crearPago(clienteId, monto, formaPago,
                    (fecha!=null? Date.valueOf(fecha): null), obs);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void aplicarPago(Integer pagoId, List<CxcAplicacionItemDto> items) {
        try {
            repo.aplicarPago(pagoId, items);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void anularPago(Integer pagoId, String motivo) {
        try {
            repo.anularPago(pagoId, motivo);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> estadoCuenta(Integer clienteId) {
        try {
            return repo.estadoCuenta(clienteId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
