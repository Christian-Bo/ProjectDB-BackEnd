package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.VentaDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import com.nexttechstore.nexttech_backend.dto.VentaResumenDto;
import com.nexttechstore.nexttech_backend.service.api.VentasService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}")
    public VentaDto obtener(@PathVariable int id) {
        return service.obtenerVentaPorId(id);
    }

    @GetMapping
    public List<VentaResumenDto> listar(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String numeroVenta,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        LocalDate d = (desde != null && !desde.isBlank()) ? LocalDate.parse(desde) : null;
        LocalDate h = (hasta != null && !hasta.isBlank()) ? LocalDate.parse(hasta) : null;
        return service.listarVentas(d, h, clienteId, numeroVenta, page, size);
    }
}
