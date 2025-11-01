package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.CotizacionCreateRequestDto;
import com.nexttechstore.nexttech_backend.repository.orm.CotizacionesCommandRepository;
import com.nexttechstore.nexttech_backend.repository.orm.CotizacionesCommandRepository.ResultadoBasico;
import com.nexttechstore.nexttech_backend.service.api.CotizacionesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionesController {

    private final CotizacionesService service;
    private final CotizacionesCommandRepository cmdRepo; // <-- para ajustar tipo de pago cuando sea R

    public CotizacionesController(CotizacionesService service,
                                  CotizacionesCommandRepository cmdRepo) {
        this.service = service;
        this.cmdRepo = cmdRepo;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "50") Integer size
    ) {
        return ResponseEntity.ok(service.listar(desde, hasta, clienteId, numero, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CotizacionCreateRequestDto req) {
        int id = service.crear(req);
        return ResponseEntity.ok(Map.of("code", 0, "message", "OK", "id", id));
    }

    /**
     * Convierte una cotización a venta. Por compatibilidad, si no envías tipoPago, se asume 'C' (contado).
     * Si envías tipoPago=R (crédito), primero se crea la venta (contado por el SP actual) y luego se
     * cambia a crédito con sp_ventas_edit_header (abre/ajusta CxC).
     */
    @PostMapping("/{id}/to-venta")
    public ResponseEntity<?> convertirAVenta(
            @PathVariable int id,
            @RequestParam("bodegaId") int bodegaId,
            @RequestParam("serieId")  int serieId,
            @RequestParam(value = "cajeroId", required = false) Integer cajeroId,
            @RequestParam(value = "tipoPago", required = false, defaultValue = "C") String tipoPago
    ) {
        // 1) Crea la venta (el servicio llama al SP que hoy siempre deja 'C')
        int ventaId = service.convertirAVenta(id, bodegaId, serieId, cajeroId);

        // 2) Si pidieron Crédito, cambia a 'R' sin tocar el schema/SP de conversión
        char tp = (tipoPago == null || tipoPago.isBlank()) ? 'C' : Character.toUpperCase(tipoPago.charAt(0));
        if (tp == 'R') {
            ResultadoBasico upd = cmdRepo.ventaEditarTipoPago(ventaId, 'R', cajeroId);
            if (upd.code() != 0) {
                // devolvemos el id creado para que el front no lo pierda, pero indicamos el error
                return ResponseEntity.status(500).body(Map.of(
                        "venta_id", ventaId,
                        "error", "No se pudo cambiar a crédito",
                        "detail", upd.message()
                ));
            }
        }

        return ResponseEntity.ok(Map.of("code", 0, "message", "OK", "venta_id", ventaId));
    }
}
