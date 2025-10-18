package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.VentaDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import com.nexttechstore.nexttech_backend.dto.VentaResumenDto;
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

    @GetMapping
    public List<VentaResumenDto> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String numeroVenta,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "50") Integer size
    ) {
        return service.listarVentas(desde, hasta, clienteId, numeroVenta, page, size);
    }

    @GetMapping("/{id}")
    public VentaDto obtener(@PathVariable int id) {
        return service.obtenerVentaPorId(id);
    }
}
