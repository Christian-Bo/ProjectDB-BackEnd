package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.BodegaDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ClienteDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.EmpleadoDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ProductoStockDto;
import com.nexttechstore.nexttech_backend.repository.sql.CatalogosQueryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de catálogos para poblar selects del frontend (modal Nueva Venta).
 * No modifica nada: solo lectura.
 */
@RestController
@RequestMapping("/api/catalogos")
public class CatalogosController {

    private final CatalogosQueryRepository repo;

    public CatalogosController(CatalogosQueryRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<ClienteDto>> clientes(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(repo.buscarClientes(q, limit));
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<EmpleadoDto>> empleados(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(repo.buscarEmpleados(q, limit));
    }

    @GetMapping("/bodegas")
    public ResponseEntity<List<BodegaDto>> bodegas(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(repo.buscarBodegas(q, limit));
    }

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoStockDto>> productos(
            @RequestParam(value = "bodegaId") Integer bodegaId,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "limit", defaultValue = "50") int limit
    ) {
        return ResponseEntity.ok(repo.buscarProductosConStock(bodegaId, q, limit));
    }

    @GetMapping("/productos/{id}/existencia")
    public ResponseEntity<ProductoStockDto> existenciaProducto(
            @PathVariable("id") Integer productoId,
            @RequestParam("bodegaId") Integer bodegaId
    ) {
        return ResponseEntity.of(repo.obtenerProductoConStock(bodegaId, productoId));
    }
}
