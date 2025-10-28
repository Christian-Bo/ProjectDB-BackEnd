package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.service.api.ListaPreciosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listas-precios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ListaPreciosController {

    private final ListaPreciosService listaPreciosService;

    // ===== MAESTRO =====

    @PostMapping
    public ResponseEntity<ApiResponse<ListaPreciosResponseDTO>> create(@Valid @RequestBody ListaPreciosRequestDTO request) {
        log.info("POST /api/listas-precios - Creando lista de precios: {}", request.getNombre());
        ApiResponse<ListaPreciosResponseDTO> response = listaPreciosService.create(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @Valid @RequestBody ListaPreciosRequestDTO request) {
        log.info("PUT /api/listas-precios/{} - Actualizando lista", id);
        ApiResponse<Void> response = listaPreciosService.update(id, request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        log.info("DELETE /api/listas-precios/{} - Eliminando lista", id);
        ApiResponse<Void> response = listaPreciosService.delete(id);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Integer id) {
        log.info("PATCH /api/listas-precios/{}/activar - Activando lista", id);
        ApiResponse<Void> response = listaPreciosService.activate(id);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ListaPreciosResponseDTO>> getById(@PathVariable Integer id) {
        log.info("GET /api/listas-precios/{} - Obteniendo lista", id);
        return listaPreciosService.getById(id)
                .map(lista -> ResponseEntity.ok(ApiResponse.success("Lista encontrada", lista)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Lista no encontrada")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ListaPreciosResponseDTO>>> getAll(
            @RequestParam(required = false, defaultValue = "true") Boolean soloActivas) {
        log.info("GET /api/listas-precios - Obteniendo todas las listas");
        ApiResponse<List<ListaPreciosResponseDTO>> response = listaPreciosService.getAll(soloActivas);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/copiar")
    public ResponseEntity<ApiResponse<Map<String, Object>>> copiar(@Valid @RequestBody ListaPreciosCopiarRequestDTO request) {
        log.info("POST /api/listas-precios/copiar - Copiando lista");
        ApiResponse<Map<String, Object>> response = listaPreciosService.copiar(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // ===== DETALLE =====

    @PostMapping("/{listaId}/detalles")
    public ResponseEntity<ApiResponse<ListaPreciosDetalleResponseDTO>> createDetalle(
            @PathVariable Integer listaId,
            @Valid @RequestBody ListaPreciosDetalleRequestDTO request) {
        log.info("POST /api/listas-precios/{}/detalles - Creando detalle", listaId);
        request.setListaId(listaId);
        ApiResponse<ListaPreciosDetalleResponseDTO> response = listaPreciosService.createDetalle(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @PutMapping("/detalles/{detalleId}")
    public ResponseEntity<ApiResponse<Void>> updateDetalle(
            @PathVariable Integer detalleId,
            @Valid @RequestBody ListaPreciosDetalleRequestDTO request) {
        log.info("PUT /api/listas-precios/detalles/{} - Actualizando detalle", detalleId);
        ApiResponse<Void> response = listaPreciosService.updateDetalle(detalleId, request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @DeleteMapping("/detalles/{detalleId}")
    public ResponseEntity<ApiResponse<Void>> deleteDetalle(@PathVariable Integer detalleId) {
        log.info("DELETE /api/listas-precios/detalles/{} - Eliminando detalle", detalleId);
        ApiResponse<Void> response = listaPreciosService.deleteDetalle(detalleId);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping("/{listaId}/detalles")
    public ResponseEntity<ApiResponse<List<ListaPreciosDetalleResponseDTO>>> getDetalles(
            @PathVariable Integer listaId,
            @RequestParam(required = false, defaultValue = "false") Boolean soloVigentes) {
        log.info("GET /api/listas-precios/{}/detalles - Obteniendo detalles", listaId);
        ApiResponse<List<ListaPreciosDetalleResponseDTO>> response =
                listaPreciosService.getDetallesByLista(listaId, soloVigentes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/aplicar-incremento")
    public ResponseEntity<ApiResponse<Map<String, Object>>> aplicarIncremento(
            @Valid @RequestBody ListaPreciosIncrementoRequestDTO request) {
        log.info("POST /api/listas-precios/aplicar-incremento - Aplicando incremento");
        ApiResponse<Map<String, Object>> response = listaPreciosService.aplicarIncremento(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }
}
