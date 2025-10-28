package com.nexttechstore.nexttech_backend.service.impl;


import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.repository.sp.MarcasSpRepository;
import com.nexttechstore.nexttech_backend.service.api.MarcasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarcasServiceImpl implements MarcasService {

    private final MarcasSpRepository marcasRepository;

    @Override
    @Transactional
    public ApiResponse<MarcaResponseDTO> create(MarcaRequestDTO request) {
        log.info("Creando marca: {}", request.getNombre());

        // Validaciones adicionales de negocio aquí si es necesario
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            return ApiResponse.error("El nombre de la marca es requerido");
        }

        return marcasRepository.create(request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> update(Integer id, MarcaRequestDTO request) {
        log.info("Actualizando marca ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("ID de marca inválido");
        }

        return marcasRepository.update(id, request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        log.info("Eliminando marca ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("ID de marca inválido");
        }

        return marcasRepository.delete(id);
    }

    @Override
    @Transactional
    public ApiResponse<Void> activate(Integer id) {
        log.info("Activando marca ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("ID de marca inválido");
        }

        return marcasRepository.activate(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MarcaResponseDTO> getById(Integer id) {
        log.info("Obteniendo marca ID: {}", id);

        if (id == null || id <= 0) {
            return Optional.empty();
        }

        return marcasRepository.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<MarcaResponseDTO>> getAll(Boolean soloActivas, PageRequest pageRequest) {
        log.info("Obteniendo todas las marcas. Solo activas: {}", soloActivas);
        return marcasRepository.getAll(soloActivas != null ? soloActivas : true, pageRequest);
    }
}