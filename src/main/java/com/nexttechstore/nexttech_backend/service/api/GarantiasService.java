package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.productos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import java.util.List;
import java.util.Optional;

public interface GarantiasService {
    ApiResponse<GarantiaResponseDTO> create(GarantiaRequestDTO request);
    ApiResponse<Void> update(Integer id, GarantiaRequestDTO request);
    Optional<GarantiaResponseDTO> getById(Integer id);
    ApiResponse<List<GarantiaResponseDTO>> getVigentes(Integer diasAlerta);
    ApiResponse<List<GarantiaResponseDTO>> getByCliente(Integer clienteId, Boolean soloVigentes);
    ApiResponse<Void> marcarUsada(Integer id, String observaciones);
}