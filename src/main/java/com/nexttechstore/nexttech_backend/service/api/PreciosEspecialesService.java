package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.precios.PrecioEspecialCreateRequestDto;
import com.nexttechstore.nexttech_backend.dto.precios.PrecioEspecialUpdateRequestDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PreciosEspecialesService {
    List<Map<String, Object>> listar(String texto, Boolean activo, LocalDate desde, LocalDate hasta, int page, int size);
    Map<String, Object> getById(int id);

    Map<String, Object> crear(PrecioEspecialCreateRequestDto req);
    Map<String, Object> actualizar(int id, PrecioEspecialUpdateRequestDto req);
    Map<String, Object> setActivo(int id, boolean valor);
    Map<String, Object> eliminar(int id);

    Map<String, Object> resolverPrecio(int clienteId, int productoId, LocalDate fecha);
}
