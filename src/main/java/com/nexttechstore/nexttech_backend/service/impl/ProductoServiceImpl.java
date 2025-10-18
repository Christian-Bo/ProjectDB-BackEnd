package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.catalogos.ProductoDto;
import com.nexttechstore.nexttech_backend.repository.sp.ProductosSpRepository;
import com.nexttechstore.nexttech_backend.service.api.ProductoService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductosSpRepository repo;

    public ProductoServiceImpl(ProductosSpRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<ProductoDto> listar() {
        return repo.listar();
    }

    @Override
    public ProductoDto obtener(int id) {
        return repo.buscarPorId(id);
    }

    @Override
    public int crear(ProductoDto dto) {
        return repo.crear(dto);
    }

    @Override
    public int actualizar(int id, ProductoDto dto) {
        return repo.editar(id, dto);
    }

    @Override
    public int eliminar(int id) {
        return repo.eliminar(id);
    }
}
