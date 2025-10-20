package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import java.util.List;
import java.util.Map;

public interface ReglasMargenService {
    ApiResponse<ReglaMargenResponseDTO> create(ReglaMargenRequestDTO request);
    ApiResponse<Void> update(Integer id, ReglaMargenRequestDTO request);
    ApiResponse<Void> delete(Integer id);
    ApiResponse<List<ReglaMargenResponseDTO>> getAll();
    ApiResponse<Map<String, Object>> aplicarMasivo(ReglaMargenAplicarMasivoRequestDTO request);
}
