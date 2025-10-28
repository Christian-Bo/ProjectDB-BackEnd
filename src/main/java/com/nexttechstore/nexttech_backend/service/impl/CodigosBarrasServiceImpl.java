package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.repository.sp.CodigosBarrasSpRepository;
import com.nexttechstore.nexttech_backend.service.api.CodigosBarrasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodigosBarrasServiceImpl implements CodigosBarrasService {

    private final CodigosBarrasSpRepository codigosBarrasRepository;

    @Override
    @Transactional
    public ApiResponse<CodigoBarrasResponseDTO> create(CodigoBarrasRequestDTO request) {
        log.info("Creando código de barras para producto ID: {}", request.getProductoId());
        return codigosBarrasRepository.create(request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> update(Integer id, CodigoBarrasRequestDTO request) {
        log.info("Actualizando código de barras ID: {}", id);
        return codigosBarrasRepository.update(id, request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        log.info("Eliminando código de barras ID: {}", id);
        return codigosBarrasRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CodigoBarrasResponseDTO>> getByProducto(Integer productoId, Boolean soloActivos) {
        log.info("Obteniendo códigos de barras de producto ID: {}", productoId);
        return codigosBarrasRepository.getByProducto(productoId, soloActivos != null ? soloActivos : true);
    }
}