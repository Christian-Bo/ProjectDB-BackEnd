package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.repository.sp.CxcPagosSpRepository;
import com.nexttechstore.nexttech_backend.service.api.CxcPagosService;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Service
public class CxcPagosServiceImpl implements CxcPagosService {

    private final CxcPagosSpRepository spRepo;

    public CxcPagosServiceImpl(CxcPagosSpRepository spRepo) {
        this.spRepo = spRepo;
    }

    @Override
    public List<Map<String, Object>> listarPagos(Integer clienteId, Date desde, Date hasta) {
        try {
            return spRepo.listarPagos(clienteId, desde, hasta);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> listarAplicacionesPorPago(Integer pagoId) {
        try {
            return spRepo.listarAplicacionesPorPago(pagoId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
