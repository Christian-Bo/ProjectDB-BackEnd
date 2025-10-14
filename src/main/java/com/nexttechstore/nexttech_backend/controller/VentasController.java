package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.VentaDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import com.nexttechstore.nexttech_backend.dto.VentaResumenDto;
import com.nexttechstore.nexttech_backend.service.api.VentasService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

    @PostMapping("/{id}/anular")
    public Map<String, Object> anular(@PathVariable int id,
                                      @RequestParam(required = false) String motivo) {
        // Llama al repo SP a través del service (si prefieres, lo pasamos por el service)
        try {
            // Si tu Service aún no expone anular, puedes inyectar VentasSpRepository directamente.
            // Aquí asumo que tienes el bean spRepo disponible en el service (ajusta si hace falta).
            // Opción rápida: expón anular en VentasService y dele en VentasServiceImpl.
            throw new UnsupportedOperationException("Exponer método anular en VentasService/VentasServiceImpl o inyectar VentasSpRepository aquí.");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
