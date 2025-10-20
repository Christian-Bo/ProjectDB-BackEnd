package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.productos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.service.api.GarantiasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/garantias")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GarantiaController {

    private final GarantiasService garantiasService;

    @PostMapping
    public ResponseEntity<ApiResponse<GarantiaResponseDTO>> create(@Valid @RequestBody GarantiaRequestDTO request) {
        log.info("POST /api/garantias - Creando garantía");
        ApiResponse<GarantiaResponseDTO> response = garantiasService.create(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @Valid @RequestBody GarantiaRequestDTO request) {
        log.info("PUT /api/garantias/{} - Actualizando garantía", id);
        ApiResponse<Void> response = garantiasService.update(id, request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GarantiaResponseDTO>> getById(@PathVariable Integer id) {
        log.info("GET /api/garantias/{} - Obteniendo garantía", id);
        return garantiasService.getById(id)
                .map(garantia -> ResponseEntity.ok(ApiResponse.success("Garantía encontrada", garantia)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Garantía no encontrada")));
    }

    @GetMapping("/vigentes")
    public ResponseEntity<ApiResponse<List<GarantiaResponseDTO>>> getVigentes(
            @RequestParam(required = false, defaultValue = "30") Integer diasAlerta) {
        log.info("GET /api/garantias/vigentes - Obteniendo garantías vigentes");
        ApiResponse<List<GarantiaResponseDTO>> response = garantiasService.getVigentes(diasAlerta);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<GarantiaResponseDTO>>> getByCliente(
            @PathVariable Integer clienteId,
            @RequestParam(required = false, defaultValue = "false") Boolean soloVigentes) {
        log.info("GET /api/garantias/cliente/{} - Obteniendo garantías del cliente", clienteId);
        ApiResponse<List<GarantiaResponseDTO>> response = garantiasService.getByCliente(clienteId, soloVigentes);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/marcar-usada")
    public ResponseEntity<ApiResponse<Void>> marcarUsada(
            @PathVariable Integer id,
            @RequestBody(required = false) String observaciones) {
        log.info("PATCH /api/garantias/{}/marcar-usada - Marcando garantía como usada", id);
        ApiResponse<Void> response = garantiasService.marcarUsada(id, observaciones);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }
}