package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.model.compras.CompraPago;
import com.nexttechstore.nexttech_backend.model.compras.CompraPagoCrearRequest;
import com.nexttechstore.nexttech_backend.model.compras.CompraPagoEditarRequest;
import com.nexttechstore.nexttech_backend.service.api.ComprasPagosService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST para pagos de compras.
 * Nota: Se asume encabezado "X-User-Id" para @UsuarioId.
 */
@RestController
@RequestMapping("/api/compras/pagos")
public class ComprasPagosController {

    private final ComprasPagosService service;

    public ComprasPagosController(ComprasPagosService service) {
        this.service = service;
    }

    /**
     * Listado "tipo select": permite armar combos o grids sin pedir ID.
     * Filtros opcionales:
     *  - compraId: lista solo de esa compra
     *  - texto: busca en forma_pago y referencia (LIKE)
     */
    @GetMapping
    public List<CompraPago> listar(@RequestParam(required = false) Integer compraId,
                                   @RequestParam(required = false) String texto) {
        return service.listar(compraId, texto);
    }

    @PostMapping
    public CompraPago crear(@RequestHeader("X-User-Id") Integer usuarioId,
                            @Valid @RequestBody CompraPagoCrearRequest body) {
        return service.crear(usuarioId, body);
    }

    @PutMapping("/{id}")
    public CompraPago editar(@RequestHeader("X-User-Id") Integer usuarioId,
                             @PathVariable Integer id,
                             @Valid @RequestBody CompraPagoEditarRequest body) {
        return service.editar(usuarioId, id, body);
    }

    @DeleteMapping("/{id}")
    public Integer eliminar(@RequestHeader("X-User-Id") Integer usuarioId,
                            @PathVariable Integer id) {
        return service.eliminar(usuarioId, id);
    }
}
