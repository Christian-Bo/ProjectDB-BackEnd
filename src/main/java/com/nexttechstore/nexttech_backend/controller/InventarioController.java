package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.AlertaInventarioDto;
import com.nexttechstore.nexttech_backend.dto.InventarioDto;
import com.nexttechstore.nexttech_backend.repository.sp.InventarioSpRepository;
import com.nexttechstore.nexttech_backend.service.api.InventarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping(value = {"/api/inventario", "/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class InventarioController {

    private static final Logger log = LoggerFactory.getLogger(InventarioController.class);

    @Autowired
    private InventarioService inventarioService;

    // Para devolver resultados "raw" donde conviene (movimientos/alertas)
    @Autowired
    private InventarioSpRepository inventarioSpRepository;

    /* ----------------------------- helpers ----------------------------- */
    private static String rootCauseMsg(Throwable t) {
        if (t == null) return null;
        Throwable c = t;
        while (c.getCause() != null && c.getCause() != c) c = c.getCause();
        return c.getMessage();
    }

    private <T> ResponseEntity<List<T>> okOrFallback(Exception e, List<T> fallback, String warnMsg) {
        HttpHeaders h = new HttpHeaders();
        if (e != null) {
            h.add("X-NT-Warning", (warnMsg == null ? "fallback" : warnMsg) + ": " + rootCauseMsg(e));
            log.error("[Inventario] {}", warnMsg, e);
        }
        return new ResponseEntity<>(fallback == null ? Collections.emptyList() : fallback, h, HttpStatus.OK);
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /* ----------------------------- Health ----------------------------- */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("ok", true);
        ok.put("controller", "InventarioController");
        return ok;
    }

    @GetMapping("/health/db")
    public ResponseEntity<?> healthDb(@RequestParam(required = false) Integer bodegaId) {
        try {
            List<InventarioDto> inv = inventarioService.listarInventario(bodegaId);
            int n = (inv == null) ? 0 : inv.size();
            return ResponseEntity.ok(Map.of("ok", true, "count", n));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "Fallo al consultar inventario", "detail", rootCauseMsg(e)));
        }
    }

    /* ----------------------------- STOCK ----------------------------- */
    @GetMapping({"", "/", "/inventario", "/inventarios", "/stock"})
    public ResponseEntity<List<InventarioDto>> listarInventario(@RequestParam(required = false) Integer bodegaId) {
        Instant t0 = Instant.now();
        try {
            log.info("[Inventario] GET stock bodegaId={}", bodegaId);
            List<InventarioDto> inventario = inventarioService.listarInventario(bodegaId);
            if (inventario == null) inventario = Collections.emptyList();
            log.info("[Inventario] -> {} registros ({} ms)", inventario.size(), Duration.between(t0, Instant.now()).toMillis());
            return ResponseEntity.ok(inventario);
        } catch (Exception e) {
            return okOrFallback(e, Collections.emptyList(), "No se pudo obtener el inventario (fallback)");
        }
    }

    /* ----------------------------- STOCK por producto ----------------------------- */
    @GetMapping({"/producto/{id}", "/inventario/producto/{id}"})
    public ResponseEntity<List<InventarioDto>> inventarioPorProducto(@PathVariable Integer id) {
        Instant t0 = Instant.now();
        try {
            log.info("[Inventario] GET producto/{}", id);
            List<InventarioDto> inventario = inventarioService.inventarioPorProducto(id);
            if (inventario == null) inventario = Collections.emptyList();
            log.info("[Inventario] producto {} -> {} registros ({} ms)", id, inventario.size(), Duration.between(t0, Instant.now()).toMillis());
            return ResponseEntity.ok(inventario);
        } catch (Exception e) {
            return okOrFallback(e, Collections.emptyList(), "No se pudo obtener inventario por producto (fallback)");
        }
    }

    /* ----------------------------- STOCK por bodega ----------------------------- */
    @GetMapping({"/bodega/{id}", "/inventario/bodega/{id}"})
    public ResponseEntity<List<InventarioDto>> inventarioPorBodega(@PathVariable Integer id) {
        Instant t0 = Instant.now();
        try {
            log.info("[Inventario] GET bodega/{}", id);
            List<InventarioDto> inventario = inventarioService.inventarioPorBodega(id);
            if (inventario == null) inventario = Collections.emptyList();
            log.info("[Inventario] bodega {} -> {} registros ({} ms)", id, inventario.size(), Duration.between(t0, Instant.now()).toMillis());
            return ResponseEntity.ok(inventario);
        } catch (Exception e) {
            return okOrFallback(e, Collections.emptyList(), "No se pudo obtener inventario por bodega (fallback)");
        }
    }

    /* ----------------------------- MOVIMIENTOS (KARDEX) ----------------------------- */
    @GetMapping({
            "/kardex",
            "/movimientos",
            "/inventario/kardex",
            "/inventario/movimientos",
            "/inventarios/kardex",
            "/inventarios/movimientos"
    })
    public ResponseEntity<List<Map<String, Object>>> listarMovimientosRaw(
            @RequestParam(required = false) Integer productoId,
            @RequestParam(required = false) Integer bodegaId,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) String tipo
    ) {
        Instant t0 = Instant.now();
        try {
            String fDesde = trimOrNull(fechaDesde);
            String fHasta = trimOrNull(fechaHasta);
            String tMov   = trimOrNull(tipo);
            log.info("[Inventario] GET movimientos RAW productoId={} bodegaId={} desde={} hasta={} tipo={}", productoId, bodegaId, fDesde, fHasta, tMov);

            List<Map<String, Object>> data = inventarioSpRepository.listarMovimientos(fDesde, fHasta, productoId, bodegaId, tMov);
            if (data == null) data = Collections.emptyList();

            log.info("[Inventario] movimientos RAW -> {} registros ({} ms)", data.size(), Duration.between(t0, Instant.now()).toMillis());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return okOrFallback(e, Collections.emptyList(), "No se pudo obtener movimientos (raw/fallback)");
        }
    }

    /* ----------------------------- ALERTAS ----------------------------- */
    @GetMapping({"/alertas", "/inventario/alertas", "/alertas-inventario", "/inventarios/alertas"})
    public ResponseEntity<List<Map<String, Object>>> listarAlertasRaw(
            @RequestParam(required = false) Integer bodegaId,
            @RequestParam(required = false) String tipoAlerta,
            @RequestParam(required = false) String activa
    ) {
        Instant t0 = Instant.now();
        try {
            log.info("[Inventario] GET alertas RAW bodegaId={} tipoAlerta={} activa={}", bodegaId, tipoAlerta, activa);

            List<Map<String, Object>> alertas = inventarioSpRepository.listarAlertas(bodegaId);
            if (alertas == null) alertas = Collections.emptyList();

            log.info("[Inventario] alertas RAW -> {} registros ({} ms)", alertas.size(), Duration.between(t0, Instant.now()).toMillis());
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            return okOrFallback(e, Collections.emptyList(), "No se pudieron obtener las alertas (raw/fallback)");
        }
    }
}
