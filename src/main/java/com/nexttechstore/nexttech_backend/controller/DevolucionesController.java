package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.DevolucionCreateRequest;
import com.nexttechstore.nexttech_backend.service.api.DevolucionesService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionesController {

    private final DevolucionesService service;

    public DevolucionesController(DevolucionesService service) {
        this.service = service;
    }

    // Crear
    @PostMapping
    public Map<String,Object> crear(@Valid @RequestBody DevolucionCreateRequest req) {
        int id = service.crear(req);
        return Map.of("devolucionId", id, "status", "OK");
    }

    // Anular
    // Ejemplo: POST /api/devoluciones/20/anular?usuarioId=1
    @PostMapping("/{id}/anular")
    public Map<String,Object> anular(@PathVariable int id,
                                     @RequestParam int usuarioId) {
        service.anular(id, usuarioId);
        return Map.of("devolucionId", id, "status", "ANULADA");
    }

    // --- NUEVOS: LECTURA ---

    // Detalle por id (usa sp_devoluciones_get_by_id)
    @GetMapping("/{id}")
    public Map<String,Object> obtener(@PathVariable int id) {
        return service.obtenerPorId(id);
    }

    // Listado con filtros (usa sp_devoluciones_list)
    // Ejemplo:
    // GET /api/devoluciones?desde=2025-10-01&hasta=2025-10-31&ventaId=11&clienteId=1&page=0&size=50
    @GetMapping
    public List<Map<String,Object>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer ventaId,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "50") Integer size
    ) {
        return service.listar(desde, hasta, ventaId, clienteId, page, size);
    }
}
