package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.repository.sp.ClienteListaPreciosSpRepository;
import com.nexttechstore.nexttech_backend.service.api.ClienteListaPreciosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteListaPreciosServiceImpl implements ClienteListaPreciosService {

    private final ClienteListaPreciosSpRepository clienteListaRepository;

    @Override
    @Transactional
    public ApiResponse<ClienteListaPreciosResponseDTO> asignarLista(ClienteAsignarListaRequestDTO request) {
        log.info("Asignando lista ID: {} a cliente ID: {}", request.getListaId(), request.getClienteId());
        return clienteListaRepository.asignarLista(request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> removerLista(Integer clienteId) {
        log.info("Removiendo lista de precios de cliente ID: {}", clienteId);
        return clienteListaRepository.removerLista(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteListaPreciosResponseDTO> getListaByCliente(Integer clienteId) {
        log.info("Obteniendo lista de precios de cliente ID: {}", clienteId);
        return clienteListaRepository.getListaByCliente(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientePrecioProductoResponseDTO> getPrecioProducto(Integer clienteId, Integer productoId, String fecha) {
        log.info("Obteniendo precio de producto ID: {} para cliente ID: {}", productoId, clienteId);
        return clienteListaRepository.getPrecioProducto(clienteId, productoId, fecha);
    }
}