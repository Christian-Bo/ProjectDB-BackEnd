package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.DevolucionCreateRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DevolucionesService {
    int crear(DevolucionCreateRequest req);
    void anular(int devolucionId, int usuarioId);

    Map<String,Object> obtenerPorId(int devolucionId);
    List<Map<String,Object>> listar(LocalDate desde,
                                    LocalDate hasta,
                                    Integer ventaId,
                                    Integer clienteId,
                                    Integer page,
                                    Integer size);
}
