package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.service.api.MarcasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MarcaController {

    private final MarcasService marcasService;

    @PostMapping
    public ResponseEntity<ApiResponse<MarcaResponseDTO>> create(@Valid @RequestBody MarcaRequestDTO request) {
        log.info("POST /api/marcas - Creando marca: {}", request.getNombre());
        ApiResponse<MarcaResponseDTO> response = marcasService.create(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @Valid @RequestBody MarcaRequestDTO request) {
        log.info("PUT /api/marcas/{} - Actualizando marca", id);
        ApiResponse<Void> response = marcasService.update(id, request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        log.info("DELETE /api/marcas/{} - Eliminando marca", id);
        ApiResponse<Void> response = marcasService.delete(id);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Integer id) {
        log.info("PATCH /api/marcas/{}/activar - Activando marca", id);
        ApiResponse<Void> response = marcasService.activate(id);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MarcaResponseDTO>> getById(@PathVariable Integer id) {
        log.info("GET /api/marcas/{} - Obteniendo marca", id);
        return marcasService.getById(id)
                .map(marca -> ResponseEntity.ok(ApiResponse.success("Marca encontrada", marca)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Marca no encontrada")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MarcaResponseDTO>>> getAll(
            @RequestParam(required = false, defaultValue = "true") Boolean soloActivas,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        log.info("GET /api/marcas - Obteniendo todas las marcas");

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .build();

        ApiResponse<List<MarcaResponseDTO>> response = marcasService.getAll(soloActivas, pageRequest);
        return ResponseEntity.ok(response);
    }
}
