package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.service.api.FacturasService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
public class FacturasController {

    private final FacturasService service;

    public FacturasController(FacturasService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> emitir(@RequestBody Map<String,Object> body) {
        int ventaId = (int) ( (Number) body.get("ventaId") ).intValue();
        int serieId = (int) ( (Number) body.get("serieId") ).intValue();
        int emitidaPor = (int) ( (Number) body.getOrDefault("emitidaPor", 1) ).intValue(); // ajusta a tu sesión
        int id = service.emitir(ventaId, serieId, emitidaPor);
        return ResponseEntity.ok(Map.of("code", 0, "message", "OK", "id", id));
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String serie,
            @RequestParam(required = false) String numero,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(service.listar(desde, hasta, serie, numero, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable int id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable int id) {
        byte[] pdf = service.generarPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=factura-"+id+".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
