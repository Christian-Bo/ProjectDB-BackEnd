package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.repository.sp.*;
import com.nexttechstore.nexttech_backend.service.api.ListaPreciosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListaPreciosServiceImpl implements ListaPreciosService {

    private final ListaPreciosSpRepository listaPreciosRepository;
    private final ListaPreciosDetalleSpRepository detalleRepository;

    // Maestro
    @Override
    @Transactional
    public ApiResponse<ListaPreciosResponseDTO> create(ListaPreciosRequestDTO request) {
        log.info("Creando lista de precios: {}", request.getNombre());
        return listaPreciosRepository.create(request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> update(Integer id, ListaPreciosRequestDTO request) {
        log.info("Actualizando lista de precios ID: {}", id);
        return listaPreciosRepository.update(id, request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        log.info("Eliminando lista de precios ID: {}", id);
        return listaPreciosRepository.delete(id);
    }

    @Override
    @Transactional
    public ApiResponse<Void> activate(Integer id) {
        log.info("Activando lista de precios ID: {}", id);
        return listaPreciosRepository.activate(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ListaPreciosResponseDTO> getById(Integer id) {
        log.info("Obteniendo lista de precios ID: {}", id);
        return listaPreciosRepository.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ListaPreciosResponseDTO>> getAll(Boolean soloActivas) {
        log.info("Obteniendo todas las listas de precios");
        return listaPreciosRepository.getAll(soloActivas != null ? soloActivas : true);
    }

    @Override
    @Transactional
    public ApiResponse<Map<String, Object>> copiar(ListaPreciosCopiarRequestDTO request) {
        log.info("Copiando lista de precios ID: {}", request.getListaOrigenId());
        return listaPreciosRepository.copiar(request);
    }

    // Detalle
    @Override
    @Transactional
    public ApiResponse<ListaPreciosDetalleResponseDTO> createDetalle(ListaPreciosDetalleRequestDTO request) {
        log.info("Creando detalle de lista ID: {} para producto ID: {}", request.getListaId(), request.getProductoId());

        if (request.getVigenteHasta() != null && request.getVigenteHasta().isBefore(request.getVigenteDesde())) {
            return ApiResponse.error("La fecha hasta no puede ser anterior a la fecha desde");
        }

        return detalleRepository.create(request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> updateDetalle(Integer id, ListaPreciosDetalleRequestDTO request) {
        log.info("Actualizando detalle de lista ID: {}", id);

        if (request.getVigenteHasta() != null && request.getVigenteHasta().isBefore(request.getVigenteDesde())) {
            return ApiResponse.error("La fecha hasta no puede ser anterior a la fecha desde");
        }

        return detalleRepository.update(id, request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteDetalle(Integer id) {
        log.info("Eliminando detalle de lista ID: {}", id);
        return detalleRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ListaPreciosDetalleResponseDTO>> getDetallesByLista(Integer listaId, Boolean soloVigentes) {
        log.info("Obteniendo detalles de lista ID: {}", listaId);
        return detalleRepository.getByLista(listaId, soloVigentes != null ? soloVigentes : false);
    }

    @Override
    @Transactional
    public ApiResponse<Map<String, Object>> aplicarIncremento(ListaPreciosIncrementoRequestDTO request) {
        log.info("Aplicando incremento de {}% a lista ID: {}", request.getPorcentajeIncremento(), request.getListaId());
        return detalleRepository.aplicarIncremento(request);
    }
}