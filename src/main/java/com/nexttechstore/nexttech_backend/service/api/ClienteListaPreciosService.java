package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import java.util.Optional;

public interface ClienteListaPreciosService {
    ApiResponse<ClienteListaPreciosResponseDTO> asignarLista(ClienteAsignarListaRequestDTO request);
    ApiResponse<Void> removerLista(Integer clienteId);
    Optional<ClienteListaPreciosResponseDTO> getListaByCliente(Integer clienteId);
    Optional<ClientePrecioProductoResponseDTO> getPrecioProducto(Integer clienteId, Integer productoId, String fecha);
}