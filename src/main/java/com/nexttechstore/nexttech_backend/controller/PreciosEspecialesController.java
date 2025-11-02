package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.precios.PrecioEspecialCreateRequestDto;
import com.nexttechstore.nexttech_backend.dto.precios.PrecioEspecialUpdateRequestDto;
import com.nexttechstore.nexttech_backend.service.api.PreciosEspecialesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/precios-especiales")
public class PreciosEspecialesController {

    private final PreciosEspecialesService service;

    public PreciosEspecialesController(PreciosEspecialesService service) {
        this.service = service;
    }

    // GET list
    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size
    ) {
        return ResponseEntity.ok(service.listar(texto, activo, desde, hasta, page, size));
    }

    // GET by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        var r = service.getById(id);
        return (r == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(r);
    }

    // POST create
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody PrecioEspecialCreateRequestDto req) {
        var r = service.crear(req);
        int status = (int) r.getOrDefault("status", -1);
        return (status == 0) ? ResponseEntity.ok(r) : ResponseEntity.unprocessableEntity().body(r);
    }

    // PUT update
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody PrecioEspecialUpdateRequestDto req) {
        var r = service.actualizar(id, req);
        int status = (int) r.getOrDefault("status", -1);
        return (status == 0) ? ResponseEntity.ok(r) : ResponseEntity.unprocessableEntity().body(r);
    }

    // POST activar/inactivar ?valor=true|false ó ?valor=1|0
    @PostMapping("/{id}/activo")
    public ResponseEntity<?> setActivo(@PathVariable int id, @RequestParam String valor) {
        boolean v;
        if ("1".equalsIgnoreCase(valor) || "true".equalsIgnoreCase(valor)) v = true;
        else if ("0".equalsIgnoreCase(valor) || "false".equalsIgnoreCase(valor)) v = false;
        else return ResponseEntity.badRequest().body(
                    java.util.Map.of("status", 400, "message", "Parametro 'valor' inválido (use true/false o 1/0)", "id", id, "valor", valor)
            );
        var r = service.setActivo(id, v);
        int status = (int) r.getOrDefault("status", -1);
        return (status == 0) ? ResponseEntity.ok(r) : ResponseEntity.unprocessableEntity().body(r);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        var r = service.eliminar(id);
        int status = (int) r.getOrDefault("status", -1);
        return (status == 0) ? ResponseEntity.ok(r) : ResponseEntity.unprocessableEntity().body(r);
    }

    // GET resolver precio: /resolver?clienteId=1&productoId=2&fecha=2025-11-01
    @GetMapping("/resolver")
    public ResponseEntity<?> resolver(
            @RequestParam int clienteId,
            @RequestParam int productoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        var r = service.resolverPrecio(clienteId, productoId, fecha);
        int status = (int) r.getOrDefault("status", -1);
        return (status == 0) ? ResponseEntity.ok(r) : ResponseEntity.unprocessableEntity().body(r);
    }
}
