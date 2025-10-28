package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.TransferenciaDto;
import com.nexttechstore.nexttech_backend.service.api.TransferenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transferencias")
@CrossOrigin(origins = "*")
public class TransferenciaController {

    @Autowired
    private TransferenciaService transferenciaService;

    @GetMapping
    public ResponseEntity<List<TransferenciaDto>> listarTransferencias(
            @RequestParam(required = false) Integer bodegaOrigenId,
            @RequestParam(required = false) Integer bodegaDestinoId,
            @RequestParam(required = false) String estado) {
        try {
            List<TransferenciaDto> transferencias = transferenciaService.listarTransferencias(
                    bodegaOrigenId, bodegaDestinoId, estado);
            return ResponseEntity.ok(transferencias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferenciaDto> obtenerTransferenciaPorId(@PathVariable Integer id) {
        try {
            TransferenciaDto transferencia = transferenciaService.obtenerTransferenciaPorId(id);
            if (transferencia != null) {
                return ResponseEntity.ok(transferencia);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
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

    @PutMapping("/{id}/aprobar")
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

    @PutMapping("/{id}/recibir")
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

    @PutMapping("/{id}/cancelar")
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