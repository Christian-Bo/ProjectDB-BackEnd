package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.catalogos.CodigoBarrasDto;
import com.nexttechstore.nexttech_backend.repository.sp.CodigosBarrasSpRepository;
import com.nexttechstore.nexttech_backend.service.api.CodigoBarrasService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodigoBarrasServiceImpl implements CodigoBarrasService {

    private final CodigosBarrasSpRepository repo;

    public CodigoBarrasServiceImpl(CodigosBarrasSpRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<CodigoBarrasDto> listar() {
        return repo.listar();
    }

    @Override
    public CodigoBarrasDto obtener(int id) {
        return repo.buscarPorId(id);
    }

    @Override
    public int crear(CodigoBarrasDto dto) {
        return repo.crear(dto);
    }

    @Override
    public int actualizar(int id, CodigoBarrasDto dto) {
        return repo.editar(id, dto);
    }

    @Override
    public int eliminar(int id) {
        return repo.eliminar(id);
    }
}
