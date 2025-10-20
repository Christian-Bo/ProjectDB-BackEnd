package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.productos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.service.api.ProductosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductosService productosService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> create(@Valid @RequestBody ProductoRequestDTO request) {
        log.info("POST /api/productos - Creando producto: {}", request.getNombre());
        ApiResponse<ProductoResponseDTO> response = productosService.create(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoRequestDTO request) {
        log.info("PUT /api/productos/{} - Actualizando producto", id);
        ApiResponse<Void> response = productosService.update(id, request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        log.info("DELETE /api/productos/{} - Eliminando producto", id);
        ApiResponse<Void> response = productosService.delete(id);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Integer id) {
        log.info("PATCH /api/productos/{}/activar - Activando producto", id);
        ApiResponse<Void> response = productosService.activate(id);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PatchMapping("/{id}/descontinuar")
    public ResponseEntity<ApiResponse<Void>> descontinuar(
            @PathVariable Integer id,
            @RequestBody(required = false) String motivo) {
        log.info("PATCH /api/productos/{}/descontinuar - Descontinuando producto", id);
        ApiResponse<Void> response = productosService.descontinuar(id, motivo);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> getById(@PathVariable Integer id) {
        log.info("GET /api/productos/{} - Obteniendo producto", id);
        return productosService.getById(id)
                .map(producto -> ResponseEntity.ok(ApiResponse.success("Producto encontrado", producto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Producto no encontrado")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> getAll(
            @RequestParam(required = false, defaultValue = "true") Boolean soloActivos,
            @RequestParam(required = false) Integer marcaId,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        log.info("GET /api/productos - Obteniendo todos los productos");

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .build();

        ApiResponse<List<ProductoResponseDTO>> response = productosService.getAll(
                soloActivos, marcaId, categoriaId, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductoSearchDTO>>> search(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "true") Boolean soloActivos) {
        log.info("GET /api/productos/search?q={} - Buscando productos", q);
        ApiResponse<List<ProductoSearchDTO>> response = productosService.search(q, soloActivos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/codigo-barras/{codigoBarras}")
    public ResponseEntity<ApiResponse<ProductoSearchDTO>> buscarPorCodigoBarras(
            @PathVariable String codigoBarras) {
        log.info("GET /api/productos/codigo-barras/{} - Buscando producto por código de barras", codigoBarras);
        return productosService.buscarPorCodigoBarras(codigoBarras)
                .map(producto -> ResponseEntity.ok(ApiResponse.success("Producto encontrado", producto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Producto no encontrado")));
    }
}