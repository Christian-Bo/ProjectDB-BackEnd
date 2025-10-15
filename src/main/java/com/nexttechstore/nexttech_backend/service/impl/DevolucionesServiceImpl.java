package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.DevolucionCreateRequest;
import com.nexttechstore.nexttech_backend.repository.sp.DevolucionesSpRepository;
import com.nexttechstore.nexttech_backend.service.api.DevolucionesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DevolucionesServiceImpl implements DevolucionesService {

    private final DevolucionesSpRepository repo;

    public DevolucionesServiceImpl(DevolucionesSpRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public int crear(DevolucionCreateRequest req) {
        try {
            return repo.crearDevolucion(req);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public void anular(int devolucionId, int usuarioId) {
        try {
            repo.anularDevolucion(devolucionId, usuarioId);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Map<String, Object> obtenerPorId(int devolucionId) {
        try {
            return repo.getDevolucionById(devolucionId);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<Map<String, Object>> listar(LocalDate desde,
                                            LocalDate hasta,
                                            Integer ventaId,
                                            Integer clienteId,
                                            Integer page,
                                            Integer size) {
        try {
            return repo.listarDevoluciones(desde, hasta, ventaId, clienteId, page, size);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
