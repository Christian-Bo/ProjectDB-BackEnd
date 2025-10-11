package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.PagoRequestDto;
import com.nexttechstore.nexttech_backend.exception.BadRequestException;
import com.nexttechstore.nexttech_backend.repository.sp.CxcSpRepository;
import com.nexttechstore.nexttech_backend.service.api.CxcService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CxcServiceImpl implements CxcService {

    private final CxcSpRepository repo;

    public CxcServiceImpl(CxcSpRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public int aplicarPago(PagoRequestDto req) {
        if (req.getMonto() == null || req.getMonto() <= 0) {
            throw new BadRequestException("El monto debe ser > 0");
        }
        try {
            return repo.aplicarPago(req);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
