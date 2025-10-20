package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.*;
import com.nexttechstore.nexttech_backend.security.AllowedRoles;
import com.nexttechstore.nexttech_backend.service.api.VentasService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@AllowedRoles({"OPERACIONES"})
@RestController
@RequestMapping("/api/ventas")
public class VentasController {

    private final VentasService service;

    public VentasController(VentasService service) {
        this.service = service;
    }

    @PostMapping
    public Map<String, Object> crear(@Valid @RequestBody VentaRequestDto req) {
        int id = service.registrar(req);
        return Map.of("ventaId", id, "status", "OK");
    }

    @PostMapping("/{id}/anular")
    public Map<String, Object> anular(@PathVariable int id,
                                      @RequestParam(required = false) String motivo) {
        service.anular(id, motivo);
        return Map.of("ventaId", id, "status", "ANULADA");
    }

    @PutMapping("/{id}/header")
    public Map<String, Object> editarHeader(@PathVariable int id,
                                            @RequestBody VentaHeaderEditDto dto) {
        service.editarHeader(id, dto);
        return Map.of("ventaId", id, "status", "OK");
    }

    @PutMapping("/{id}/detalle")
    public Map<String, Object> editarDetalle(@PathVariable int id,
                                             @RequestBody List<VentaDetalleEditItemDto> items) {
        service.editarDetalle(id, items);
        return Map.of("ventaId", id, "status", "OK", "items", items.size());
    }

    // LISTAR
    @GetMapping
    public List<VentaResumenDto> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String numeroVenta,
            @RequestParam(required = false, defaultValue = "false") Boolean incluirAnuladas, // ⬅️ NUEVO
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "50") Integer size
    ) {
        return service.listarVentas(desde, hasta, clienteId, numeroVenta, incluirAnuladas, page, size);
    }

    @GetMapping("/{id}")
    public VentaDto obtener(@PathVariable int id) {
        return service.obtenerVentaPorId(id);
    }
}
