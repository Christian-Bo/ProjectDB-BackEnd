package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.precios.PrecioEspecialCreateRequestDto;
import com.nexttechstore.nexttech_backend.dto.precios.PrecioEspecialUpdateRequestDto;
import com.nexttechstore.nexttech_backend.repository.orm.PreciosEspecialesCommandRepository;
import com.nexttechstore.nexttech_backend.repository.orm.PreciosEspecialesQueryRepository;
import com.nexttechstore.nexttech_backend.service.api.PreciosEspecialesService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PreciosEspecialesServiceImpl implements PreciosEspecialesService {

    private final PreciosEspecialesQueryRepository query;
    private final PreciosEspecialesCommandRepository command;

    public PreciosEspecialesServiceImpl(PreciosEspecialesQueryRepository query,
                                        PreciosEspecialesCommandRepository command) {
        this.query = query;
        this.command = command;
    }

    @Override
    public List<Map<String, Object>> listar(String texto, Boolean activo, LocalDate desde, LocalDate hasta, int page, int size) {
        return query.listar(texto, activo, desde, hasta, page, size);
    }

    @Override
    public Map<String, Object> getById(int id) {
        return query.getById(id);
    }

    @Override
    public Map<String, Object> crear(PrecioEspecialCreateRequestDto req) {
        var r = command.crear(req);
        Map<String, Object> out = new HashMap<>();
        out.put("status", r.code());
        out.put("message", r.message());
        out.put("id", r.id());
        return out;
    }

    @Override
    public Map<String, Object> actualizar(int id, PrecioEspecialUpdateRequestDto req) {
        var r = command.actualizar(id, req);
        Map<String, Object> out = new HashMap<>();
        out.put("status", r.code());
        out.put("message", r.message());
        out.put("id", id);
        return out;
    }

    @Override
    public Map<String, Object> setActivo(int id, boolean valor) {
        var r = command.setActivo(id, valor);
        Map<String, Object> out = new HashMap<>();
        out.put("status", r.code());
        out.put("message", r.message());
        out.put("id", id);
        out.put("activo", valor ? 1 : 0);
        return out;
    }

    @Override
    public Map<String, Object> eliminar(int id) {
        var r = command.eliminar(id);
        Map<String, Object> out = new HashMap<>();
        out.put("status", r.code());
        out.put("message", r.message());
        out.put("id", id);
        return out;
    }

    @Override
    public Map<String, Object> resolverPrecio(int clienteId, int productoId, LocalDate fecha) {
        var r = command.resolver(clienteId, productoId, fecha);
        Map<String, Object> out = new HashMap<>();
        out.put("status", r.code());
        out.put("message", r.message());
        out.put("clienteId", clienteId);
        out.put("productoId", productoId);
        out.put("fecha", fecha);
        out.put("precio", r.precio());
        out.put("fuente", r.fuente());
        return out;
    }
}
