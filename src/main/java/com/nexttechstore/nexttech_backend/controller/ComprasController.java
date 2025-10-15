package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.model.compras.*;
import com.nexttechstore.nexttech_backend.service.api.ComprasService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Endpoints REST para Compras.
 * Convención: la lógica vive en SPs; el controlador valida y pasa el payload.
 *
 * Además, se agregan endpoints de CATÁLOGOS para combos (proveedores, bodegas,
 * empleados, productos) para no ingresar IDs manualmente en el frontend.
 */
@RestController
@RequestMapping("/api/compras")
public class ComprasController {

    private final ComprasService service;

    public ComprasController(ComprasService service) {
        this.service = service;
    }

    // ========= Listar (filtros opcionales) =========
    @GetMapping
    public List<CompraListItem> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaDel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaAl,
            @RequestParam(required = false) Integer proveedorId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String texto
    ) {
        return service.listar(fechaDel, fechaAl, proveedorId, estado, texto);
    }

    // ========= Obtener una compra completa =========
    @GetMapping("/{id}")
    public CompraFull obtener(@PathVariable int id) {
        return service.obtenerPorId(id);
    }

    // ========= Crear compra (cabecera + detalle TVP) =========
    @PostMapping
    public int crear(@Valid @RequestBody CompraCrearRequest req) {
        return service.crear(req);
    }

    // ========= Editar cabecera =========
    @PutMapping("/{id}/cabecera")
    public int editarCabecera(@PathVariable int id, @Valid @RequestBody CompraEditarCabeceraRequest req) {
        req.setCompraId(id);
        return service.editarCabecera(req);
    }

    // ========= Agregar detalle (1..n líneas por TVP) =========
    @PostMapping("/{id}/detalles")
    public int agregarDetalle(@PathVariable int id,
                              @RequestParam int usuarioId,
                              @Valid @RequestBody List<CompraDetalleRequest> lineas) {
        return service.agregarDetalle(usuarioId, id, lineas);
    }

    // ========= Editar una línea del detalle =========
    @PutMapping("/{id}/detalles/{detalleId}")
    public int editarDetalle(@PathVariable int id, @PathVariable int detalleId,
                             @Valid @RequestBody CompraEditarDetalleRequest req) {
        req.setDetalleId(detalleId);
        return service.editarDetalle(req);
    }

    // ========= Quitar una línea del detalle =========
    @DeleteMapping("/{id}/detalles/{detalleId}")
    public int quitarDetalle(@PathVariable int id, @PathVariable int detalleId, @RequestParam int usuarioId) {
        return service.quitarDetalle(usuarioId, detalleId);
    }

    // ========= Anular compra =========
    @PostMapping("/{id}/anular")
    public int anular(@PathVariable int id, @Valid @RequestBody CompraAnularRequest req) {
        req.setCompraId(id);
        return service.anular(req);
    }

    // =====================================================================
    // =====================  C A T Á L O G O S  ===========================
    // =====================================================================
    // Todos cuelgan de /api/compras/catalogos para mantener arquitectura sin nuevos controllers

    /** Proveedores (opcional ?activo=true) */
    @GetMapping("/catalogos/proveedores")
    public List<Map<String,Object>> catProveedores(@RequestParam(required = false) Boolean activo) {
        return service.catalogoProveedores(activo);
    }

    /** Bodegas */
    @GetMapping("/catalogos/bodegas")
    public List<Map<String,Object>> catBodegas() {
        return service.catalogoBodegas();
    }

    /** Empleados */
    @GetMapping("/catalogos/empleados")
    public List<Map<String,Object>> catEmpleados() {
        return service.catalogoEmpleados();
    }

    /**
     * Productos (parámetros opcionales):
     *  - texto: busca por nombre (LIKE %texto%)
     *  - limit: TOP n resultados (default 50)
     */
    @GetMapping("/catalogos/productos")
    public List<Map<String,Object>> catProductos(@RequestParam(required = false) String texto,
                                                 @RequestParam(required = false) Integer limit) {
        return service.catalogoProductos(texto, limit);
    }
}
