package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import java.util.List;
import java.util.Optional;

public interface MarcasService {
    ApiResponse<MarcaResponseDTO> create(MarcaRequestDTO request);
    ApiResponse<Void> update(Integer id, MarcaRequestDTO request);
    ApiResponse<Void> delete(Integer id);
    ApiResponse<Void> activate(Integer id);
    Optional<MarcaResponseDTO> getById(Integer id);
    ApiResponse<List<MarcaResponseDTO>> getAll(Boolean soloActivas, PageRequest pageRequest);
}