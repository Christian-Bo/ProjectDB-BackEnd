// src/main/java/com/nexttechstore/nexttech_backend/service/impl/DevolucionesServiceImpl.java
package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.devoluciones.DevolucionCreateRequestDto;
import com.nexttechstore.nexttech_backend.repository.orm.DevolucionesCommandRepository;
import com.nexttechstore.nexttech_backend.repository.orm.DevolucionesQueryRepository;
import com.nexttechstore.nexttech_backend.service.api.DevolucionesService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DevolucionesServiceImpl implements DevolucionesService {

    private final DevolucionesQueryRepository query;
    private final DevolucionesCommandRepository command;

    public DevolucionesServiceImpl(DevolucionesQueryRepository query,
                                   DevolucionesCommandRepository command) {
        this.query = query;
        this.command = command;
    }

    @Override
    public List<Map<String, Object>> listar(LocalDate desde, LocalDate hasta, Integer clienteId, String numero, int page, int size) {
        return query.listar(desde, hasta, clienteId, numero, page, size);
    }

    @Override
    public Map<String, Object> crear(DevolucionCreateRequestDto req) throws Exception {
        var r = command.crear(req);
        Map<String, Object> out = new HashMap<>();
        out.put("status", r.code());
        out.put("message", r.message());
        out.put("id", r.devolucionId());
        return out;
    }

    @Override
    public List<Map<String, Object>> saldosPorVenta(int ventaId) {
        return query.saldosPorVenta(ventaId);
    }

    // NUEVO
    @Override
    public Map<String, Object> obtener(int devolucionId) {
        Map<String, Object> header = query.obtenerHeader(devolucionId);
        if (header == null || header.isEmpty()) {
            throw new RuntimeException("Devolución no encontrada: id=" + devolucionId);
        }
        List<Map<String, Object>> items = query.obtenerItems(devolucionId);
        Map<String, Object> result = new HashMap<>(header);
        result.put("items", items);
        return result;
    }
}
