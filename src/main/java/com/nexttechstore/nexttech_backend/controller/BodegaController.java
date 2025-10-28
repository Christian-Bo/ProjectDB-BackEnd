package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.BodegaDto;
import com.nexttechstore.nexttech_backend.service.api.BodegaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bodegas")
@CrossOrigin(origins = "*")
public class BodegaController {

    @Autowired
    private BodegaService bodegaService;

    @GetMapping
    public ResponseEntity<List<BodegaDto>> listarBodegas() {
        try {
            List<BodegaDto> bodegas = bodegaService.listarBodegas();
            return ResponseEntity.ok(bodegas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BodegaDto> obtenerBodegaPorId(@PathVariable Integer id) {
        try {
            BodegaDto bodega = bodegaService.obtenerBodegaPorId(id);
            if (bodega != null) {
                return ResponseEntity.ok(bodega);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearBodega(@RequestBody BodegaDto bodegaDto) {
        try {
            BodegaDto nuevaBodega = bodegaService.crearBodega(bodegaDto);
            Map<String, Object> response = new HashMap<>();

            if (nuevaBodega != null) {
                response.put("success", true);
                response.put("message", "Bodega creada exitosamente");
                response.put("data", nuevaBodega);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            response.put("success", false);
            response.put("message", "Error al crear la bodega");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarBodega(@PathVariable Integer id, @RequestBody BodegaDto bodegaDto) {
        try {
            BodegaDto bodegaActualizada = bodegaService.actualizarBodega(id, bodegaDto);
            Map<String, Object> response = new HashMap<>();

            if (bodegaActualizada != null) {
                response.put("success", true);
                response.put("message", "Bodega actualizada exitosamente");
                response.put("data", bodegaActualizada);
                return ResponseEntity.ok(response);
            }

            response.put("success", false);
            response.put("message", "Bodega no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarBodega(@PathVariable Integer id) {
        try {
            bodegaService.eliminarBodega(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bodega eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}