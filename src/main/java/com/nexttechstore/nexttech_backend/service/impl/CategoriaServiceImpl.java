package com.nexttechstore.nexttech_backend.service.impl;

import org.springframework.stereotype.Service;
import com.nexttechstore.nexttech_backend.dto.catalogos.CategoriaDto;
import com.nexttechstore.nexttech_backend.repository.sp.CategoriasSpRepository;
import com.nexttechstore.nexttech_backend.service.api.CategoriaService;
import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriasSpRepository repo;

    public CategoriaServiceImpl(CategoriasSpRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<CategoriaDto> listar() {
        return repo.listar();
    }

    @Override
    public CategoriaDto obtener(int id) {
        return repo.buscarPorId(id);
    }

    @Override
    public int crear(CategoriaDto dto) {
        return repo.crear(dto.getNombre(), dto.getDescripcion(), dto.getCategoriaPadreId());
    }

    @Override
    public int actualizar(int id, CategoriaDto dto) {
        return repo.editar(id, dto.getNombre(), dto.getDescripcion(), dto.getCategoriaPadreId());
    }

    @Override
    public int eliminar(int id) {
        return repo.eliminar(id);
    }
}
