package com.nexttechstore.nexttech_backend.service.impl;



import com.nexttechstore.nexttech_backend.dto.productos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.repository.sp.GarantiasSpRepository;
import com.nexttechstore.nexttech_backend.service.api.GarantiasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GarantiasServiceImpl implements GarantiasService {

    private final GarantiasSpRepository garantiasRepository;

    @Override
    @Transactional
    public ApiResponse<GarantiaResponseDTO> create(GarantiaRequestDTO request) {
        log.info("Creando garantía para venta ID: {}", request.getVentaId());

        if (request.getFechaVencimiento().isBefore(request.getFechaInicio())) {
            return ApiResponse.error("La fecha de vencimiento no puede ser anterior a la fecha de inicio");
        }

        return garantiasRepository.create(request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> update(Integer id, GarantiaRequestDTO request) {
        log.info("Actualizando garantía ID: {}", id);

        if (request.getFechaVencimiento().isBefore(request.getFechaInicio())) {
            return ApiResponse.error("La fecha de vencimiento no puede ser anterior a la fecha de inicio");
        }

        return garantiasRepository.update(id, request);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GarantiaResponseDTO> getById(Integer id) {
        log.info("Obteniendo garantía ID: {}", id);
        return garantiasRepository.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<GarantiaResponseDTO>> getVigentes(Integer diasAlerta) {
        log.info("Obteniendo garantías vigentes con {} días de alerta", diasAlerta);
        return garantiasRepository.getVigentes(diasAlerta != null ? diasAlerta : 30);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<GarantiaResponseDTO>> getByCliente(Integer clienteId, Boolean soloVigentes) {
        log.info("Obteniendo garantías de cliente ID: {}", clienteId);
        return garantiasRepository.getByCliente(clienteId, soloVigentes != null ? soloVigentes : false);
    }

    @Override
    @Transactional
    public ApiResponse<Void> marcarUsada(Integer id, String observaciones) {
        log.info("Marcando garantía ID: {} como usada", id);
        return garantiasRepository.marcarUsada(id, observaciones);
    }
}
