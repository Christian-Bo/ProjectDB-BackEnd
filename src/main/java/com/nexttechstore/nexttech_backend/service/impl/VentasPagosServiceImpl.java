package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.repository.sp.VentasPagosSpRepository;
import com.nexttechstore.nexttech_backend.service.api.VentasPagosService;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Service
public class VentasPagosServiceImpl implements VentasPagosService {

    private final VentasPagosSpRepository repo;

    public VentasPagosServiceImpl(VentasPagosSpRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Map<String, Object>> listarPagos(Integer ventaId, Integer clienteId, Date desde, Date hasta) {
        try { return repo.listarPagos(ventaId, clienteId, desde, hasta); }
        catch (Exception e){ throw new RuntimeException(e.getMessage(), e); }
    }

    @Override
    public int crearPago(int ventaId, String formaPago, java.math.BigDecimal monto, String referencia) {
        try { return repo.crearPago(ventaId, formaPago, monto, referencia); }
        catch (Exception e){ throw new RuntimeException(e.getMessage(), e); }
    }

    @Override
    public void eliminarPago(int pagoId) {
        try { repo.eliminarPago(pagoId); }
        catch (Exception e){ throw new RuntimeException(e.getMessage(), e); }
    }

    @Override public Map<String, Object> obtenerPago(int pagoId) {
        try { return repo.obtenerPago(pagoId); } catch (Exception e){ throw new RuntimeException(e.getMessage(), e); }
    }
    @Override public void actualizarPago(int pagoId, String formaPago, java.math.BigDecimal monto, String referencia) {
        try { repo.actualizarPago(pagoId, formaPago, monto, referencia); } catch (Exception e){ throw new RuntimeException(e.getMessage(), e); }
    }
}
