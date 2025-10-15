package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.PagoRequestDto;
import com.nexttechstore.nexttech_backend.repository.sp.CxcSpRepository;
import com.nexttechstore.nexttech_backend.service.impl.CxcServiceImpl;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cxc")
public class CxcController {

    private final CxcServiceImpl service;

    public CxcController(CxcServiceImpl service) {
        this.service = service;
    }

    // [Compat] Tu método original; hoy lanza BadRequest porque no trae clienteId/formapago.
    @PostMapping("/pagos")
    public Map<String, Object> aplicarPago(@Valid @RequestBody PagoRequestDto req) {
        int pagoId = service.aplicarPago(req);
        return Map.of("pagoId", pagoId, "status", "OK");
    }

    // ---- NUEVOS ENDPOINTS ----

    /** Crear pago (caja): cliente + monto + forma de pago + (opcional) observaciones */
    @PostMapping("/pagos/crear")
    public Map<String, Object> crearPago(
            @RequestParam Integer clienteId,
            @RequestParam BigDecimal monto,
            @RequestParam(defaultValue = "EFECTIVO") String formaPago,
            @RequestParam(required = false) String observaciones
    ) {
        int pagoId = service.crearPago(clienteId, monto, formaPago, observaciones);
        return Map.of("pagoId", pagoId, "status", "OK");
    }

    /** Aplicar pago a documentos (usa TVP dbo.tvp_cxc_aplicacion) */
    @PostMapping("/pagos/{pagoId}/aplicar")
    public Map<String, Object> aplicarPagoADocumentos(
            @PathVariable int pagoId,
            @RequestBody List<CxcSpRepository.AplicacionItem> items
    ) {
        service.aplicarPagoADocs(pagoId, items);
        return Map.of("pagoId", pagoId, "aplicaciones", items.size(), "status", "OK");
    }

    /** Anular pago */
    @PostMapping("/pagos/{pagoId}/anular")
    public Map<String, Object> anularPago(
            @PathVariable int pagoId,
            @RequestParam(required = false) String motivo
    ) {
        service.anularPago(pagoId, motivo);
        return Map.of("pagoId", pagoId, "status", "ANULADO");
    }

    /** Estado de cuenta por cliente (consulta) */
    @GetMapping("/estado-cuenta")
    public List<CxcSpRepository.EstadoCuentaRow> estadoCuenta(
            @RequestParam Integer clienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        Date d = (desde != null) ? Date.valueOf(desde) : null;
        Date h = (hasta != null) ? Date.valueOf(hasta) : null;
        return service.estadoCuenta(clienteId, d, h);
    }
}
