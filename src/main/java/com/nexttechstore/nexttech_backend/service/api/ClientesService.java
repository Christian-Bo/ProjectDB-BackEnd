package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.clientes.ClienteCreateRequestDto;
import com.nexttechstore.nexttech_backend.dto.clientes.ClienteUpdateRequestDto;

import java.util.List;
import java.util.Map;

public interface ClientesService {

    List<Map<String, Object>> listar(String texto, String estado, String tipo, int page, int size);

    Map<String, Object> getById(int id);

    List<Map<String, Object>> lite(String texto, int max);

    String nextCodigo();

    Map<String, Object> crear(ClienteCreateRequestDto req);

    Map<String, Object> actualizar(int id, ClienteUpdateRequestDto req);

    Map<String, Object> setEstado(int id, String estado);
}
