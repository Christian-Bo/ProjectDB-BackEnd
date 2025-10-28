package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.productos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import java.util.List;
import java.util.Optional;

public interface ProductosService {
    ApiResponse<ProductoResponseDTO> create(ProductoRequestDTO request);
    ApiResponse<Void> update(Integer id, ProductoRequestDTO request);
    ApiResponse<Void> delete(Integer id);
    ApiResponse<Void> activate(Integer id);
    ApiResponse<Void> descontinuar(Integer id, String motivo);
    Optional<ProductoResponseDTO> getById(Integer id);
    ApiResponse<List<ProductoResponseDTO>> getAll(Boolean soloActivos, Integer marcaId, Integer categoriaId, PageRequest pageRequest);
    ApiResponse<List<ProductoSearchDTO>> search(String criterio, Boolean soloActivos);
    Optional<ProductoSearchDTO> buscarPorCodigoBarras(String codigoBarras);
}
