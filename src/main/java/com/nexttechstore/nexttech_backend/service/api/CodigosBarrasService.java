package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import java.util.List;

public interface CodigosBarrasService {
    ApiResponse<CodigoBarrasResponseDTO> create(CodigoBarrasRequestDTO request);
    ApiResponse<Void> update(Integer id, CodigoBarrasRequestDTO request);
    ApiResponse<Void> delete(Integer id);
    ApiResponse<List<CodigoBarrasResponseDTO>> getByProducto(Integer productoId, Boolean soloActivos);
}