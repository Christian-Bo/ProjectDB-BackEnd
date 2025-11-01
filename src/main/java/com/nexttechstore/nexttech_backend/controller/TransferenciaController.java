package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.TransferenciaDto;
import com.nexttechstore.nexttech_backend.service.api.TransferenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestController
@RequestMapping("/api/transferencias")
@CrossOrigin(origins = "*")
public class TransferenciaController {

    private static final Logger log = LoggerFactory.getLogger(TransferenciaController.class);

    @Autowired
    private TransferenciaService transferenciaService;

    /**
     * Listar transferencias.
     * - Si vienen filtros, se delega directo al service.
     * - Si NO vienen filtros (todos null/empty), se hace un Fallback:
     *   se consulta por cada estado (P,E,R,C), se fusionan resultados y se devuelven todos.
     * - Nunca se devuelve 500 por error del SP en el listado general; en su lugar, 200 con lista vacía.
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<TransferenciaDto>> listarTransferencias(
            @RequestParam(required = false) Integer bodegaOrigenId,
            @RequestParam(required = false) Integer bodegaDestinoId,
            @RequestParam(required = false) String estado
    ) {
        // Normalizamos estado
        String estadoNorm = (estado == null || estado.trim().isEmpty())
                ? null
                : estado.trim().toUpperCase(Locale.ROOT);

        final boolean sinFiltros = bodegaOrigenId == null && bodegaDestinoId == null && estadoNorm == null;

        try {
            if (!sinFiltros) {
                // Con filtros: llamada directa
                List<TransferenciaDto> list = transferenciaService
                        .listarTransferencias(bodegaOrigenId, bodegaDestinoId, estadoNorm);
                return ResponseEntity.ok(list != null ? list : Collections.emptyList());
            }

            // ====== Fallback SIN filtros ======
            // Muchos SP revientan con parámetros NULL; aquí consultamos por estado y unimos resultados.
            String[] estados = {"P", "E", "R", "C"};
            Map<Integer, TransferenciaDto> dedup = new LinkedHashMap<>();

            for (String est : estados) {
                try {
                    List<TransferenciaDto> parciales =
                            transferenciaService.listarTransferencias(null, null, est);

                    if (parciales != null) {
                        for (TransferenciaDto dto : parciales) {
                            if (dto != null && dto.getId() != null) {
                                dedup.put(dto.getId(), dto); // evita duplicados por id
                            }
                        }
                    }
                } catch (Exception exEstado) {
                    // No rompemos el flujo; seguimos con otros estados
                    log.warn("Fallo al listar por estado {}: {}", est, exEstado.getMessage());
                }
            }

            List<TransferenciaDto> resultado = new ArrayList<>(dedup.values());
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error("Error en listarTransferencias: {}", e.getMessage(), e);
            // Para no romper el front: devolvemos lista vacía
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<TransferenciaDto> obtenerTransferenciaPorId(@PathVariable Integer id) {
        try {
            TransferenciaDto transferencia = transferenciaService.obtenerTransferenciaPorId(id);
            if (transferencia != null) {
                return ResponseEntity.ok(transferencia);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error al obtener transferencia {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> crearTransferencia(@RequestBody TransferenciaDto transferenciaDto) {
        try {
            TransferenciaDto nuevaTransferencia = transferenciaService.crearTransferencia(transferenciaDto);
            Map<String, Object> response = new HashMap<>();

            if (nuevaTransferencia != null) {
                response.put("success", true);
                response.put("message", "Transferencia creada exitosamente");
                response.put("data", nuevaTransferencia);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            response.put("success", false);
            response.put("message", "Error al crear la transferencia");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping(value = "/{id}/aprobar", produces = "application/json")
    public ResponseEntity<Map<String, Object>> aprobarTransferencia(
            @PathVariable Integer id,
            @RequestParam Integer aprobadorId) {
        try {
            transferenciaService.aprobarTransferencia(id, aprobadorId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transferencia aprobada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping(value = "/{id}/recibir", produces = "application/json")
    public ResponseEntity<Map<String, Object>> recibirTransferencia(
            @PathVariable Integer id,
            @RequestParam Integer receptorId) {
        try {
            transferenciaService.recibirTransferencia(id, receptorId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transferencia recibida y aplicada al inventario");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping(value = "/{id}/cancelar", produces = "application/json")
    public ResponseEntity<Map<String, Object>> cancelarTransferencia(@PathVariable Integer id) {
        try {
            transferenciaService.cancelarTransferencia(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transferencia cancelada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
