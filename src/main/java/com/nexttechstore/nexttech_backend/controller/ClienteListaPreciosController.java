package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.precios.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.service.api.ClienteListaPreciosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes-listas-precios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ClienteListaPreciosController {

    private final ClienteListaPreciosService clienteListaService;

    @PostMapping("/asignar")
    public ResponseEntity<ApiResponse<ClienteListaPreciosResponseDTO>> asignarLista(
            @Valid @RequestBody ClienteAsignarListaRequestDTO request) {
        log.info("POST /api/clientes-listas-precios/asignar - Asignando lista a cliente");
        ApiResponse<ClienteListaPreciosResponseDTO> response = clienteListaService.asignarLista(request);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @DeleteMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<Void>> removerLista(@PathVariable Integer clienteId) {
        log.info("DELETE /api/clientes-listas-precios/cliente/{} - Removiendo lista", clienteId);
        ApiResponse<Void> response = clienteListaService.removerLista(clienteId);
        return ResponseEntity
                .status(response.getOk() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<ClienteListaPreciosResponseDTO>> getListaByCliente(@PathVariable Integer clienteId) {
        log.info("GET /api/clientes-listas-precios/cliente/{} - Obteniendo lista del cliente", clienteId);
        return clienteListaService.getListaByCliente(clienteId)
                .map(lista -> ResponseEntity.ok(ApiResponse.success("Lista encontrada", lista)))
                .orElse(ResponseEntity.ok(ApiResponse.success("Cliente sin lista asignada", null)));
    }

    @GetMapping("/precio")
    public ResponseEntity<ApiResponse<ClientePrecioProductoResponseDTO>> getPrecioProducto(
            @RequestParam Integer clienteId,
            @RequestParam Integer productoId,
            @RequestParam(required = false) String fecha) {
        log.info("GET /api/clientes-listas-precios/precio - Obteniendo precio para cliente {} producto {}", clienteId, productoId);
        return clienteListaService.getPrecioProducto(clienteId, productoId, fecha)
                .map(precio -> ResponseEntity.ok(ApiResponse.success("Precio obtenido", precio)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("No se pudo determinar el precio")));
    }
}
