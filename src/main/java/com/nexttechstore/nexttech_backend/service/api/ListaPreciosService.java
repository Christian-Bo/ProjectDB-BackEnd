package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import java.util.List;
import java.util.Optional;
import java.util.Map;

public interface ListaPreciosService {
    // Maestro
    ApiResponse<ListaPreciosResponseDTO> create(ListaPreciosRequestDTO request);
    ApiResponse<Void> update(Integer id, ListaPreciosRequestDTO request);
    ApiResponse<Void> delete(Integer id);
    ApiResponse<Void> activate(Integer id);
    Optional<ListaPreciosResponseDTO> getById(Integer id);
    ApiResponse<List<ListaPreciosResponseDTO>> getAll(Boolean soloActivas);
    ApiResponse<Map<String, Object>> copiar(ListaPreciosCopiarRequestDTO request);

    // Detalle
    ApiResponse<ListaPreciosDetalleResponseDTO> createDetalle(ListaPreciosDetalleRequestDTO request);
    ApiResponse<Void> updateDetalle(Integer id, ListaPreciosDetalleRequestDTO request);
    ApiResponse<Void> deleteDetalle(Integer id);
    ApiResponse<List<ListaPreciosDetalleResponseDTO>> getDetallesByLista(Integer listaId, Boolean soloVigentes);
    ApiResponse<Map<String, Object>> aplicarIncremento(ListaPreciosIncrementoRequestDTO request);
}