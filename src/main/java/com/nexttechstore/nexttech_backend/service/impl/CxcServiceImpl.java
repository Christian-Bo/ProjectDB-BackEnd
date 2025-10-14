package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.PagoRequestDto;
import com.nexttechstore.nexttech_backend.exception.BadRequestException;
import com.nexttechstore.nexttech_backend.repository.sp.CxcSpRepository;
import com.nexttechstore.nexttech_backend.service.api.CxcService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

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
            // Adaptación: PagoRequestDto traía documentoId; aquí creamos un pago "caja"
            // y luego el frontend puede aplicar contra documentos con el otro endpoint.
            // Si prefieres "pagar y aplicar a un documento" en una sola llamada, lo hacemos luego.
            // Por ahora necesitamos el clienteId (si tu DTO no lo tiene, crea uno para pagos).
            throw new BadRequestException("Para crear pago se requiere clienteId (agrega un DTO PagoCrearRequest con clienteId, monto, formaPago)");
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    // Métodos útiles extra (llamados por controller nuevos):
    @Transactional
    public int crearPago(Integer clienteId, BigDecimal monto, String formaPago, String obs) {
        try {
            return repo.crearPago(clienteId, monto, formaPago, obs);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional
    public void aplicarPagoADocs(int pagoId, List<CxcSpRepository.AplicacionItem> items) {
        try {
            repo.aplicarPagoADocumentos(pagoId, items);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional
    public void anularPago(int pagoId, String motivo) {
        try {
            repo.anularPago(pagoId, motivo);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<CxcSpRepository.EstadoCuentaRow> estadoCuenta(int clienteId, Date desde, Date hasta) {
        try {
            return repo.estadoDeCuenta(clienteId, desde, hasta);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
