package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.model.compras.CompraPago;
import com.nexttechstore.nexttech_backend.model.compras.CompraPagoCrearRequest;
import com.nexttechstore.nexttech_backend.model.compras.CompraPagoEditarRequest;
import com.nexttechstore.nexttech_backend.repository.sp.ComprasPagosSpRepository;
import com.nexttechstore.nexttech_backend.service.api.ComprasPagosService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComprasPagosServiceSpImpl implements ComprasPagosService {

    private final ComprasPagosSpRepository repo;

    public ComprasPagosServiceSpImpl(ComprasPagosSpRepository repo) {
        this.repo = repo;
    }

    @Override @Transactional(readOnly = true)
    public List<CompraPago> listar(Integer compraId, String texto) {
        return repo.listar(compraId, texto);
    }

    @Override @Transactional
    public CompraPago crear(Integer usuarioId, CompraPagoCrearRequest req) {
        return repo.crear(usuarioId, req);
    }

    @Override @Transactional
    public CompraPago editar(Integer usuarioId, Integer id, CompraPagoEditarRequest req) {
        return repo.editar(usuarioId, id, req);
    }

    @Override @Transactional
    public Integer eliminar(Integer usuarioId, Integer id) {
        return repo.eliminar(usuarioId, id);
    }
}
