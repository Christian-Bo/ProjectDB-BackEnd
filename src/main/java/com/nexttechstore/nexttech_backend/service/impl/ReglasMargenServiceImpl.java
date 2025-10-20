package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.repository.sp.ReglasMargenSpRepository;
import com.nexttechstore.nexttech_backend.service.api.ReglasMargenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReglasMargenServiceImpl implements ReglasMargenService {

    private final ReglasMargenSpRepository reglasMargenRepository;

    @Override
    @Transactional
    public ApiResponse<ReglaMargenResponseDTO> create(ReglaMargenRequestDTO request) {
        log.info("Creando regla de margen: {}%", request.getMargenPct());

        if (request.getCategoriaId() == null && request.getMarcaId() == null) {
            return ApiResponse.error("Debe especificar al menos una categoría o una marca");
        }

        if (request.getMargenPct().doubleValue() < 0) {
            return ApiResponse.error("El margen no puede ser negativo");
        }

        return reglasMargenRepository.create(request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> update(Integer id, ReglaMargenRequestDTO request) {
        log.info("Actualizando regla de margen ID: {}", id);

        if (request.getCategoriaId() == null && request.getMarcaId() == null) {
            return ApiResponse.error("Debe especificar al menos una categoría o una marca");
        }

        return reglasMargenRepository.update(id, request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        log.info("Eliminando regla de margen ID: {}", id);
        return reglasMargenRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ReglaMargenResponseDTO>> getAll() {
        log.info("Obteniendo todas las reglas de margen");
        return reglasMargenRepository.getAll();
    }

    @Override
    @Transactional
    public ApiResponse<Map<String, Object>> aplicarMasivo(ReglaMargenAplicarMasivoRequestDTO request) {
        log.info("Aplicando reglas de margen masivamente");
        return reglasMargenRepository.aplicarMasivo(request);
    }
}