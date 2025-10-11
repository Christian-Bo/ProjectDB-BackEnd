package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.model.cxp.*;
import com.nexttechstore.nexttech_backend.service.api.CxpService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST para Cuentas por Pagar (CxP).
 */
@RestController
@RequestMapping("/api/cxp")
public class CxpController {

    private final CxpService service;

    public CxpController(CxpService service) {
        this.service = service;
    }

    // ===== Documentos =====
    @GetMapping("/documentos")
    public List<CxpDocumento> listarDocs(@RequestParam(required = false) Integer proveedorId,
                                         @RequestParam(required = false) String texto) {
        return service.listarDocumentos(proveedorId, texto);
    }

    @PostMapping("/documentos")
    public CxpDocumento crearDoc(@RequestHeader("X-User-Id") Integer usuarioId,
                                 @Valid @RequestBody CxpDocumentoRequest body) {
        return service.crearDocumento(usuarioId, body);
    }

    @PutMapping("/documentos/{id}")
    public CxpDocumento editarDoc(@RequestHeader("X-User-Id") Integer usuarioId,
                                  @PathVariable Integer id,
                                  @Valid @RequestBody CxpDocumentoEditarRequest body) {
        return service.editarDocumento(usuarioId, id, body);
    }

    @DeleteMapping("/documentos/{id}/anular")
    public Integer anularDoc(@RequestHeader("X-User-Id") Integer usuarioId,
                             @PathVariable Integer id) {
        return service.anularDocumento(usuarioId, id);
    }

    // ===== Pagos =====
    @GetMapping("/pagos")
    public List<CxpPago> listarPagos(@RequestParam(required = false) Integer proveedorId,
                                     @RequestParam(required = false) String texto) {
        return service.listarPagos(proveedorId, texto);
    }

    @PostMapping("/pagos")
    public CxpPago crearPago(@RequestHeader("X-User-Id") Integer usuarioId,
                             @Valid @RequestBody CxpPagoRequest body) {
        return service.crearPago(usuarioId, body);
    }

    @PutMapping("/pagos/{id}")
    public CxpPago editarPago(@RequestHeader("X-User-Id") Integer usuarioId,
                              @PathVariable Integer id,
                              @Valid @RequestBody CxpPagoEditarRequest body) {
        return service.editarPago(usuarioId, id, body);
    }

    @DeleteMapping("/pagos/{id}")
    public Integer eliminarPago(@RequestHeader("X-User-Id") Integer usuarioId,
                                @PathVariable Integer id) {
        return service.eliminarPago(usuarioId, id);
    }

    @DeleteMapping("/pagos/{id}/anular")
    public Integer anularPago(@RequestHeader("X-User-Id") Integer usuarioId,
                              @PathVariable Integer id) {
        return service.anularPago(usuarioId, id);
    }

    // ===== Aplicaciones =====
    @GetMapping("/pagos/{pagoId}/aplicaciones")
    public List<CxpAplicacion> listarApl(@PathVariable Integer pagoId) {
        return service.listarAplicaciones(pagoId);
    }

    @PostMapping("/pagos/{pagoId}/aplicaciones")
    public List<CxpAplicacion> crearApl(@RequestHeader("X-User-Id") Integer usuarioId,
                                        @PathVariable Integer pagoId,
                                        @Valid @RequestBody CxpAplicacionesLoteRequest body) {
        return service.crearAplicacionesLote(usuarioId, pagoId, body.getItems());
    }
}
