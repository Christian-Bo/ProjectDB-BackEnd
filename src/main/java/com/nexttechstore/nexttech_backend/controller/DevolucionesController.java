// src/main/java/com/nexttechstore/nexttech_backend/controller/DevolucionesController.java
package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.devoluciones.DevolucionCreateRequestDto;
import com.nexttechstore.nexttech_backend.service.api.DevolucionesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionesController {

    private final DevolucionesService service;

    public DevolucionesController(DevolucionesService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String numero,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size
    ){
        return ResponseEntity.ok(service.listar(desde, hasta, clienteId, numero, page, size));
    }

    // EXISTENTE: crear devolución
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DevolucionCreateRequestDto req) throws Exception {
        Map<String, Object> r = service.crear(req);
        int status = (int) r.getOrDefault("status", -1);
        return (status == 0) ? ResponseEntity.ok(r) : ResponseEntity.unprocessableEntity().body(r);
    }

    // EXISTENTE: saldos por venta (para el modal)
    @GetMapping("/venta/{ventaId}/saldos")
    public ResponseEntity<?> saldosPorVenta(@PathVariable int ventaId) {
        return ResponseEntity.ok(service.saldosPorVenta(ventaId));
    }

    // NUEVO: encabezado + items de una devolución
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable int id){
        return ResponseEntity.ok(service.obtener(id));
    }
}
