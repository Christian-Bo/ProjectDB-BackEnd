package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.service.api.ReglasMargenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reglas-margen")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReglaMargenController {

    private final ReglasMargenService reglasMargenService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReglaMargenResponseDTO>> create(@Valid @RequestBody ReglaMargenRequestDTO request) {
        log.info("POST /api/reglas-margen - Creando regla de margen");
        ApiResponse<ReglaMargenResponseDTO> response = reglasMargenService.create(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @Valid @RequestBody ReglaMargenRequestDTO request) {
        log.info("PUT /api/reglas-margen/{} - Actualizando regla", id);
        ApiResponse<Void> response = reglasMargenService.update(id, request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        log.info("DELETE /api/reglas-margen/{} - Eliminando regla", id);
        ApiResponse<Void> response = reglasMargenService.delete(id);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReglaMargenResponseDTO>>> getAll() {
        log.info("GET /api/reglas-margen - Obteniendo todas las reglas");
        ApiResponse<List<ReglaMargenResponseDTO>> response = reglasMargenService.getAll();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/aplicar-masivo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> aplicarMasivo(
            @Valid @RequestBody ReglaMargenAplicarMasivoRequestDTO request) {
        log.info("POST /api/reglas-margen/aplicar-masivo - Aplicando reglas masivamente");
        ApiResponse<Map<String, Object>> response = reglasMargenService.aplicarMasivo(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }
}