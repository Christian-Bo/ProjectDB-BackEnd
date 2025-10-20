package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.service.api.CategoriasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriasService categoriasService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> create(@Valid @RequestBody CategoriaRequestDTO request) {
        log.info("POST /api/categorias - Creando categoría: {}", request.getNombre());
        ApiResponse<CategoriaResponseDTO> response = categoriasService.create(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaRequestDTO request) {
        log.info("PUT /api/categorias/{} - Actualizando categoría", id);
        ApiResponse<Void> response = categoriasService.update(id, request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        log.info("DELETE /api/categorias/{} - Eliminando categoría", id);
        ApiResponse<Void> response = categoriasService.delete(id);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Integer id) {
        log.info("PATCH /api/categorias/{}/activar - Activando categoría", id);
        ApiResponse<Void> response = categoriasService.activate(id);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> getById(@PathVariable Integer id) {
        log.info("GET /api/categorias/{} - Obteniendo categoría", id);
        return categoriasService.getById(id)
                .map(categoria -> ResponseEntity.ok(ApiResponse.success("Categoría encontrada", categoria)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Categoría no encontrada")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponseDTO>>> getAll(
            @RequestParam(required = false, defaultValue = "true") Boolean soloActivas,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        log.info("GET /api/categorias - Obteniendo todas las categorías");

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .build();

        ApiResponse<List<CategoriaResponseDTO>> response = categoriasService.getAll(soloActivas, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/arbol")
    public ResponseEntity<ApiResponse<List<CategoriaArbolDTO>>> getArbolCompleto(
            @RequestParam(required = false, defaultValue = "true") Boolean soloActivas) {
        log.info("GET /api/categorias/arbol - Obteniendo árbol de categorías");
        ApiResponse<List<CategoriaArbolDTO>> response = categoriasService.getArbolCompleto(soloActivas);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hijas")
    public ResponseEntity<ApiResponse<List<CategoriaResponseDTO>>> getHijas(
            @RequestParam(required = false) Integer categoriaPadreId,
            @RequestParam(required = false, defaultValue = "true") Boolean soloActivas) {
        log.info("GET /api/categorias/hijas - Obteniendo subcategorías");
        ApiResponse<List<CategoriaResponseDTO>> response = categoriasService.getHijas(categoriaPadreId, soloActivas);
        return ResponseEntity.ok(response);
    }
}
