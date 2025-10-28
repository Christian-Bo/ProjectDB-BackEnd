package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.AlertaInventarioDto;
import com.nexttechstore.nexttech_backend.dto.InventarioDto;
import com.nexttechstore.nexttech_backend.service.api.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<InventarioDto>> listarInventario(
            @RequestParam(required = false) Integer bodegaId) {
        try {
            List<InventarioDto> inventario = inventarioService.listarInventario(bodegaId);
            return ResponseEntity.ok(inventario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<List<InventarioDto>> inventarioPorProducto(@PathVariable Integer id) {
        try {
            List<InventarioDto> inventario = inventarioService.inventarioPorProducto(id);
            return ResponseEntity.ok(inventario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/bodega/{id}")
    public ResponseEntity<List<InventarioDto>> inventarioPorBodega(@PathVariable Integer id) {
        try {
            List<InventarioDto> inventario = inventarioService.inventarioPorBodega(id);
            return ResponseEntity.ok(inventario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/kardex")
    public ResponseEntity<List<InventarioDto>> listarKardex(
            @RequestParam(required = false) Integer productoId,
            @RequestParam(required = false) Integer bodegaId,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta) {
        try {
            List<InventarioDto> kardex = inventarioService.listarKardex(
                    productoId, bodegaId, fechaDesde, fechaHasta);
            return ResponseEntity.ok(kardex);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaInventarioDto>> listarAlertas(
            @RequestParam(required = false) Integer bodegaId) {
        try {
            List<AlertaInventarioDto> alertas = inventarioService.listarAlertas(bodegaId);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}