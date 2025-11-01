package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.*;
import com.nexttechstore.nexttech_backend.service.api.VentasPagosService;
import com.nexttechstore.nexttech_backend.service.api.VentasService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
public class VentasController {

    private final VentasService service;
    private final VentasPagosService ventasPagosService;

    public VentasController(VentasService service, VentasPagosService ventasPagosService) {
        this.service = service;
        this.ventasPagosService = ventasPagosService;
    }

    @PostMapping
    public Map<String, Object> crear(@Valid @RequestBody VentaRequestDto req) {
        int id = service.registrar(req);
        return Map.of("ventaId", id, "status", "OK");
    }

    @PostMapping("/{id}/anular")
    public Map<String, Object> anular(@PathVariable int id,
                                      @RequestParam(required = false) String motivo) {
        service.anular(id, motivo);
        return Map.of("ventaId", id, "status", "ANULADA");
    }

    @PutMapping("/{id}/header")
    public Map<String, Object> editarHeader(@PathVariable int id,
                                            @RequestBody VentaHeaderEditDto dto) {
        service.editarHeader(id, dto);
        return Map.of("ventaId", id, "status", "OK");
    }

    @PutMapping("/{id}/detalle")
    public Map<String, Object> editarDetalle(@PathVariable int id,
                                             @RequestBody List<VentaDetalleEditItemDto> items) {
        service.editarDetalle(id, items);
        return Map.of("ventaId", id, "status", "OK", "items", items.size());
    }

    // LISTAR
    @GetMapping
    public List<VentaResumenDto> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String numeroVenta,
            @RequestParam(required = false, defaultValue = "false") Boolean incluirAnuladas,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "50") Integer size
    ) {
        return service.listarVentas(desde, hasta, clienteId, numeroVenta, incluirAnuladas, page, size);
    }

    @GetMapping("/{id}")
    public VentaDto obtener(@PathVariable int id) {
        return service.obtenerVentaPorId(id);
    }

    // GET /api/ventas/pagos
    @GetMapping("/pagos")
    public List<Map<String,Object>> listarPagos(
            @RequestParam(required = false) Integer ventaId,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        java.sql.Date d1 = (desde != null ? java.sql.Date.valueOf(desde) : null);
        java.sql.Date d2 = (hasta != null ? java.sql.Date.valueOf(hasta) : null);
        return ventasPagosService.listarPagos(ventaId, clienteId, d1, d2);
    }

    /** POST /api/ventas/{ventaId}/pagos?forma=&monto=&referencia= */
    @PostMapping("/{ventaId}/pagos")
    public Map<String,Object> crearPagoVenta(@PathVariable int ventaId,
                                             @RequestParam String forma,
                                             @RequestParam java.math.BigDecimal monto,
                                             @RequestParam(required = false) String referencia){
        int pagoId = ventasPagosService.crearPago(ventaId, forma, monto, referencia);
        return Map.of("pagoId", pagoId, "status", "OK");
    }

    /** DELETE /api/ventas/pagos/{pagoId} */
    @DeleteMapping("/pagos/{pagoId}")
    public Map<String,Object> eliminarPagoVenta(@PathVariable int pagoId){
        ventasPagosService.eliminarPago(pagoId);
        return Map.of("pagoId", pagoId, "status", "ELIMINADO");
    }

    @GetMapping("/{id}/saldos")
    public ResponseEntity<?> obtenerSaldos(@PathVariable int id) {
        try {
            SaldosDto s = service.obtenerSaldos(id);
            if (s == null) s = new SaldosDto();

            // Defaults seguros (por si el SP devolviera nulls)
            if (s.getOrigen() == null) s.setOrigen("CONTADO");
            if (s.getTotal()  == null) s.setTotal(java.math.BigDecimal.ZERO);
            if (s.getPagado() == null) s.setPagado(java.math.BigDecimal.ZERO);
            if (s.getSaldo()  == null) s.setSaldo(s.getTotal().subtract(s.getPagado()));

            java.util.Map<String,Object> out = new java.util.LinkedHashMap<>();
            out.put("code", 0);
            out.put("message", "OK");
            out.put("origen", s.getOrigen());
            out.put("total",  s.getTotal());
            out.put("pagado", s.getPagado());
            out.put("saldo",  s.getSaldo());
            // Estos pueden ser null en CONTADO — LinkedHashMap lo permite
            out.put("documento_id", s.getDocumentoId());
            out.put("cliente_id",   s.getClienteId());

            return ResponseEntity.ok(out);
        } catch (Exception e) {
            // opcional: devolver detalle claro al front
            return ResponseEntity.internalServerError()
                    .body(java.util.Map.of("code", 500, "message", e.getMessage()));
        }
    }



}
