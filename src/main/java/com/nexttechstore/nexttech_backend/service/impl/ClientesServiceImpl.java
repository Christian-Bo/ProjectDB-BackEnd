package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.clientes.ClienteCreateRequestDto;
import com.nexttechstore.nexttech_backend.dto.clientes.ClienteUpdateRequestDto;
import com.nexttechstore.nexttech_backend.repository.orm.ClientesCommandRepository;
import com.nexttechstore.nexttech_backend.repository.orm.ClientesQueryRepository;
import com.nexttechstore.nexttech_backend.service.api.ClientesService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientesServiceImpl implements ClientesService {

    private final ClientesQueryRepository query;
    private final ClientesCommandRepository command;

    public ClientesServiceImpl(ClientesQueryRepository query, ClientesCommandRepository command) {
        this.query = query;
        this.command = command;
    }

    @Override
    public List<Map<String, Object>> listar(String texto, String estado, String tipo, int page, int size) {
        return query.listar(texto, estado, tipo, page, size);
    }

    @Override
    public Map<String, Object> getById(int id) {
        return query.getById(id);
    }

    @Override
    public List<Map<String, Object>> lite(String texto, int max) {
        return query.lite(texto, max);
    }

    @Override
    public String nextCodigo() {
        return query.nextCodigo();
    }

    @Override
    public Map<String, Object> crear(ClienteCreateRequestDto req) {
        var r = command.crear(req);
        Map<String, Object> out = new HashMap<>();
        out.put("status", r.code());
        out.put("message", r.message());
        out.put("id", r.id());
        return out;
    }

    @Override
    public Map<String, Object> actualizar(int id, ClienteUpdateRequestDto req) {
        var r = command.actualizar(id, req);
        Map<String, Object> out = new HashMap<>();
        out.put("status", r.code());
        out.put("message", r.message());
        out.put("id", id);
        return out;
    }

    @Override
    public Map<String, Object> setEstado(int id, String estado) {
        // El repositorio ya normaliza "N"->"I" y hace trim/upper;
        // aquí simplemente reenviamos y devolvemos el envoltorio uniforme.
        var r = command.setEstado(id, estado);
        Map<String, Object> out = new HashMap<>();
        out.put("status", r.code());
        out.put("message", r.message());
        out.put("id", id);
        // devolvemos el mismo texto que llegó para que el cliente vea lo que envió;
        // el SP/DB ya guarda A/I correctos
        out.put("estado", estado);
        return out;
    }
}
