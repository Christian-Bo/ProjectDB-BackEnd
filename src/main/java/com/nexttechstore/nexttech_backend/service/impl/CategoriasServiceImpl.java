package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.repository.sp.CategoriasSpRepository;
import com.nexttechstore.nexttech_backend.service.api.CategoriasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriasServiceImpl implements CategoriasService {

    private final CategoriasSpRepository categoriasRepository;

    @Override
    @Transactional
    public ApiResponse<CategoriaResponseDTO> create(CategoriaRequestDTO request) {
        log.info("Creando categoría: {}", request.getNombre());

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            return ApiResponse.error("El nombre de la categoría es requerido");
        }

        return categoriasRepository.create(request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> update(Integer id, CategoriaRequestDTO request) {
        log.info("Actualizando categoría ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("ID de categoría inválido");
        }

        return categoriasRepository.update(id, request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        log.info("Eliminando categoría ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("ID de categoría inválido");
        }

        return categoriasRepository.delete(id);
    }

    @Override
    @Transactional
    public ApiResponse<Void> activate(Integer id) {
        log.info("Activando categoría ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("ID de categoría inválido");
        }

        return categoriasRepository.activate(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaResponseDTO> getById(Integer id) {
        log.info("Obteniendo categoría ID: {}", id);

        if (id == null || id <= 0) {
            return Optional.empty();
        }

        return categoriasRepository.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CategoriaResponseDTO>> getAll(Boolean soloActivas, PageRequest pageRequest) {
        log.info("Obteniendo todas las categorías. Solo activas: {}", soloActivas);
        return categoriasRepository.getAll(soloActivas != null ? soloActivas : true, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CategoriaArbolDTO>> getArbolCompleto(Boolean soloActivas) {
        log.info("Obteniendo árbol completo de categorías");
        return categoriasRepository.getArbolCompleto(soloActivas != null ? soloActivas : true);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CategoriaResponseDTO>> getHijas(Integer categoriaPadreId, Boolean soloActivas) {
        log.info("Obteniendo subcategorías de padre ID: {}", categoriaPadreId);
        return categoriasRepository.getHijas(categoriaPadreId, soloActivas != null ? soloActivas : true);
    }
}
