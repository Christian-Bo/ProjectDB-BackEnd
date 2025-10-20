package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.service.api.CodigosBarrasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/codigos-barras")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CodigoBarrasController {

    private final CodigosBarrasService codigosBarrasService;

    @PostMapping
    public ResponseEntity<ApiResponse<CodigoBarrasResponseDTO>> create(@Valid @RequestBody CodigoBarrasRequestDTO request) {
        log.info("POST /api/codigos-barras - Creando código de barras");
        ApiResponse<CodigoBarrasResponseDTO> response = codigosBarrasService.create(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @Valid @RequestBody CodigoBarrasRequestDTO request) {
        log.info("PUT /api/codigos-barras/{} - Actualizando código de barras", id);
        ApiResponse<Void> response = codigosBarrasService.update(id, request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        log.info("DELETE /api/codigos-barras/{} - Eliminando código de barras", id);
        ApiResponse<Void> response = codigosBarrasService.delete(id);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponse<List<CodigoBarrasResponseDTO>>> getByProducto(
            @PathVariable Integer productoId,
            @RequestParam(required = false, defaultValue = "true") Boolean soloActivos) {
        log.info("GET /api/codigos-barras/producto/{} - Obteniendo códigos de barras del producto", productoId);
        ApiResponse<List<CodigoBarrasResponseDTO>> response = codigosBarrasService.getByProducto(productoId, soloActivos);
        return ResponseEntity.ok(response);
    }
}
