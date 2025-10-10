package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.model.compras.*;
import com.nexttechstore.nexttech_backend.repository.sp.ComprasSpRepository;
import com.nexttechstore.nexttech_backend.service.api.ComprasService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Implementación que delega al repositorio de SPs.
 * Usa @Transactional según lectura/escritura.
 */
@Service
public class ComprasServiceSpImpl implements ComprasService {

    private final ComprasSpRepository repo;

    public ComprasServiceSpImpl(ComprasSpRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraListItem> listar(Date fechaDel, Date fechaAl, Integer proveedorId, String estado, String texto) {
        return repo.listar(fechaDel, fechaAl, proveedorId, estado, texto);
    }

    @Override
    @Transactional(readOnly = true)
    public CompraFull obtenerPorId(int compraId) {
        return repo.obtenerPorId(compraId);
    }

    @Override
    @Transactional
    public int crear(CompraCrearRequest req) {
        return repo.crear(req);
    }

    @Override
    @Transactional
    public int editarCabecera(CompraEditarCabeceraRequest req) {
        return repo.editarCabecera(req);
    }

    @Override
    @Transactional
    public int agregarDetalle(int usuarioId, int compraId, List<CompraDetalleRequest> lineas) {
        return repo.agregarDetalle(usuarioId, compraId, lineas);
    }

    @Override
    @Transactional
    public int editarDetalle(CompraEditarDetalleRequest req) {
        return repo.editarDetalle(req);
    }

    @Override
    @Transactional
    public int quitarDetalle(int usuarioId, int detalleId) {
        return repo.quitarDetalle(usuarioId, detalleId);
    }

    @Override
    @Transactional
    public int anular(CompraAnularRequest req) {
        return repo.anular(req);
    }
}
