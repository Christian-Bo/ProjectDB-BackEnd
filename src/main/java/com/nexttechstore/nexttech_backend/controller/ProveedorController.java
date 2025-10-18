package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.ProveedorDto;
import com.nexttechstore.nexttech_backend.service.api.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.nexttechstore.nexttech_backend.security.AllowedRoles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST para Proveedor (alineado a la convención de Marca).
 * - Excepciones: usa los handlers globales ya configurados.
 * - CORS: habilitado de forma GLOBAL (CorsConfig), no se usa @CrossOrigin aquí.
 */
@AllowedRoles({"OPERACIONES"})
@RestController
@RequestMapping("/api/proveedores")

public class ProveedorController {

    private final ProveedorService service;

    public ProveedorController(ProveedorService service) {
        this.service = service;
    }

    @PostMapping
    public ProveedorDto crear(@Valid @RequestBody ProveedorDto dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public ProveedorDto actualizar(@PathVariable Integer id, @Valid @RequestBody ProveedorDto dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        service.eliminarLogico(id);
    }

    @GetMapping("/{id}")
    public ProveedorDto obtenerPorId(@PathVariable Integer id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/codigo/{codigo}")
    public ProveedorDto obtenerPorCodigo(@PathVariable String codigo) {
        return service.obtenerPorCodigo(codigo);
    }

    /** Búsqueda con paginación manual. Ej: /api/proveedores?q=ferre&activo=true&page=0&size=20 */
    @GetMapping
    public Map<String, Object> buscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<ProveedorDto> data = service.buscar(q, activo, page, size);
        long total = service.contar(q, activo);

        Map<String, Object> resp = new HashMap<>();
        resp.put("content", data);
        resp.put("page", page);
        resp.put("size", size);
        resp.put("totalElements", total);
        resp.put("totalPages", (int) Math.ceil(total / (double) Math.max(size, 1)));
        return resp;
    }

    /** Listado mínimo de empleados activos para el combo del modal */
    @GetMapping("/_empleados")
    public List<Map<String, Object>> empleadosActivosMin() {
        return service.listarEmpleadosActivosMin();
    }
}
