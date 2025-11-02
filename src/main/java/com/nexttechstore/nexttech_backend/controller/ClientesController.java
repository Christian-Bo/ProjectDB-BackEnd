package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.clientes.ClienteCreateRequestDto;
import com.nexttechstore.nexttech_backend.dto.clientes.ClienteUpdateRequestDto;
import com.nexttechstore.nexttech_backend.service.api.ClientesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClientesController {

    private final ClientesService service;

    public ClientesController(ClientesService service) {
        this.service = service;
    }

    // GET /api/clientes?texto=&estado=A&tipo=I&page=0&size=50
    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size
    ){
        return ResponseEntity.ok(service.listar(texto, estado, tipo, page, size));
    }

    // GET /api/clientes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id){
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /api/clientes/lite?texto=...&max=50
    @GetMapping("/lite")
    public ResponseEntity<?> lite(
            @RequestParam(required = false) String texto,
            @RequestParam(defaultValue = "50") Integer max
    ){
        return ResponseEntity.ok(service.lite(texto, max));
    }

    // GET /api/clientes/next-codigo
    @GetMapping("/next-codigo")
    public ResponseEntity<?> nextCodigo(){
        return ResponseEntity.ok(
                java.util.Map.of("next_codigo", service.nextCodigo())
        );
    }

    // POST /api/clientes
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ClienteCreateRequestDto req){
        var r = service.crear(req);
        int status = (int) r.getOrDefault("status", -1);
        return (status == 0) ? ResponseEntity.ok(r) : ResponseEntity.unprocessableEntity().body(r);
    }

    // PUT /api/clientes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody ClienteUpdateRequestDto req){
        var r = service.actualizar(id, req);
        int status = (int) r.getOrDefault("status", -1);
        return (status == 0) ? ResponseEntity.ok(r) : ResponseEntity.unprocessableEntity().body(r);
    }

    // POST /api/clientes/{id}/estado?estado=A|N
    @PostMapping("/{id}/estado")
    public ResponseEntity<?> setEstado(@PathVariable int id, @RequestParam String estado){
        // Normaliza: admite N como sinónimo de I (Inactivo)
        String norm = estado == null ? "" : estado.trim().toUpperCase();
        if ("N".equals(norm)) norm = "I";

        var r = service.setEstado(id, norm);
        int status = (int) r.getOrDefault("status", -1);
        return (status == 0) ? ResponseEntity.ok(r) : ResponseEntity.unprocessableEntity().body(r);
    }

}
