package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.cxc.CxcAplicacionItemDto;
import com.nexttechstore.nexttech_backend.repository.orm.CxcQueryRepository;
import com.nexttechstore.nexttech_backend.service.api.CxcService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cxc")
public class CxcController {

    private final CxcService service;
    private final CxcQueryRepository cxcQueryRepo;

    public CxcController(CxcService service, CxcQueryRepository cxcQueryRepo) {
        this.service = service;
        this.cxcQueryRepo = cxcQueryRepo;
    }

    // POST /api/cxc/pagos/crear?clienteId=&monto=&formaPago=&fechaPago=&observaciones=
    @PostMapping("/pagos/crear")
    public ResponseEntity<Map<String,Object>> crearPago(
            @RequestParam Integer clienteId,
            @RequestParam java.math.BigDecimal monto,
            @RequestParam(required = false) String formaPago,
            @RequestParam(required = false) String fechaPago, // yyyy-MM-dd (el SP no la usa; se ignora)
            @RequestParam(required = false) String observaciones
    ) {
        LocalDate fecha = (fechaPago != null && !fechaPago.isBlank() ? LocalDate.parse(fechaPago) : null);
        int pagoId = service.crearPago(clienteId, monto, formaPago, fecha, observaciones);
        return ResponseEntity.created(URI.create("/api/cxc/pagos/" + pagoId))
                .body(Map.of("pagoId", pagoId, "status", "OK"));
    }

    // POST /api/cxc/pagos/{pagoId}/aplicar   body=[{documentoId,monto}]
    @PostMapping("/pagos/{pagoId}/aplicar")
    public Map<String,Object> aplicarPago(@PathVariable Integer pagoId,
                                          @RequestBody List<CxcAplicacionItemDto> items) {
        service.aplicarPago(pagoId, items);
        return Map.of("pagoId", pagoId, "aplicado", items.size(), "status", "OK");
    }

    // POST /api/cxc/pagos/{pagoId}/anular?motivo=
    @PostMapping("/pagos/{pagoId}/anular")
    public Map<String,Object> anularPago(@PathVariable Integer pagoId,
                                         @RequestParam(required = false) String motivo) {
        service.anularPago(pagoId, motivo);
        return Map.of("pagoId", pagoId, "status", "ANULADO");
    }

    // GET /api/cxc/estado-cuenta?clienteId=
    @GetMapping("/estado-cuenta")
    public List<Map<String,Object>> estadoCuenta(@RequestParam Integer clienteId){
        return service.estadoCuenta(clienteId);
    }

    // GET /api/cxc/documentos?clienteId=&estado=&desde=&hasta=
    @GetMapping("/documentos")
    public List<Map<String,Object>> listarDocumentos(
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ){
        java.sql.Date d1 = (desde != null ? java.sql.Date.valueOf(desde) : null);
        java.sql.Date d2 = (hasta != null ? java.sql.Date.valueOf(hasta) : null);
        return cxcQueryRepo.listarDocumentos(
                clienteId,
                (estado != null && !estado.isBlank() ? estado : null),
                d1, d2
        );
    }
}
