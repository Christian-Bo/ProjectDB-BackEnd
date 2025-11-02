// src/main/java/com/nexttechstore/nexttech_backend/service/api/DevolucionesService.java
package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.devoluciones.DevolucionCreateRequestDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DevolucionesService {
    // EXISTENTES
    List<Map<String, Object>> listar(LocalDate desde, LocalDate hasta, Integer clienteId, String numero, int page, int size);
    Map<String, Object> crear(DevolucionCreateRequestDto req) throws Exception;
    List<Map<String, Object>> saldosPorVenta(int ventaId);

    // NUEVO: encabezado + items de una devolución
    Map<String, Object> obtener(int devolucionId);
}
