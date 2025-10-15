package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.seg.*;
import com.nexttechstore.nexttech_backend.service.api.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seg/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Page<UsuarioDto>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        Sort sortObj = Sort.by(sort.contains(",") ? sort.split(",")[0] : sort).ascending();
        if (sort.toLowerCase().endsWith(",desc")) sortObj = sortObj.descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return ResponseEntity.ok(usuarioService.listar(q, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody UsuarioCreateRequest req) {
        return ResponseEntity.ok(usuarioService.crear(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> actualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioUpdateRequest req) {
        return ResponseEntity.ok(usuarioService.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id); // soft-delete
        return ResponseEntity.noContent().build();
    }

    // NUEVO: cambiar estado (A/I/B)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioDto> cambiarEstado(
            @PathVariable Integer id,
            @RequestBody java.util.Map<String,String> body) {

        var estado = body.get("estado"); // "A" | "I" | "B"
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, estado));
    }
}
