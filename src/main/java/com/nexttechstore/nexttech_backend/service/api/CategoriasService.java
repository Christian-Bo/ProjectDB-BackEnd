package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import java.util.List;
import java.util.Optional;

public interface CategoriasService {
    ApiResponse<CategoriaResponseDTO> create(CategoriaRequestDTO request);
    ApiResponse<Void> update(Integer id, CategoriaRequestDTO request);
    ApiResponse<Void> delete(Integer id);
    ApiResponse<Void> activate(Integer id);
    Optional<CategoriaResponseDTO> getById(Integer id);
    ApiResponse<List<CategoriaResponseDTO>> getAll(Boolean soloActivas, PageRequest pageRequest);
    ApiResponse<List<CategoriaArbolDTO>> getArbolCompleto(Boolean soloActivas);
    ApiResponse<List<CategoriaResponseDTO>> getHijas(Integer categoriaPadreId, Boolean soloActivas);
}