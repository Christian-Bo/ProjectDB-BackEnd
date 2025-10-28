package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.AjusteInventarioDto;
import com.nexttechstore.nexttech_backend.service.api.AjusteInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ajustes")
@CrossOrigin(origins = "*")
public class AjusteInventarioController {

    @Autowired
    private AjusteInventarioService ajusteInventarioService;

    @GetMapping
    public ResponseEntity<List<AjusteInventarioDto>> listarAjustes(
            @RequestParam(required = false) Integer bodegaId,
            @RequestParam(required = false) String tipoAjuste,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta) {
        try {
            List<AjusteInventarioDto> ajustes = ajusteInventarioService.listarAjustes(
                    bodegaId, tipoAjuste, fechaDesde, fechaHasta);
            return ResponseEntity.ok(ajustes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AjusteInventarioDto> obtenerAjustePorId(@PathVariable Integer id) {
        try {
            AjusteInventarioDto ajuste = ajusteInventarioService.obtenerAjustePorId(id);
            if (ajuste != null) {
                return ResponseEntity.ok(ajuste);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearAjuste(@RequestBody AjusteInventarioDto ajusteDto) {
        try {
            AjusteInventarioDto nuevoAjuste = ajusteInventarioService.crearAjuste(ajusteDto);
            Map<String, Object> response = new HashMap<>();

            if (nuevoAjuste != null) {
                response.put("success", true);
                response.put("message", "Ajuste creado exitosamente");
                response.put("data", nuevoAjuste);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            response.put("success", false);
            response.put("message", "Error al crear el ajuste");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}