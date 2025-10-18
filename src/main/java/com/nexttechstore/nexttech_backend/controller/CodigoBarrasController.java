package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.CodigoBarrasDto;
import com.nexttechstore.nexttech_backend.service.api.CodigoBarrasService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/codigos-barras")
public class CodigoBarrasController {

    private final CodigoBarrasService service;

    public CodigoBarrasController(CodigoBarrasService service) {
        this.service = service;
    }

    @GetMapping
    public List<CodigoBarrasDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public CodigoBarrasDto obtener(@PathVariable int id) {
        return service.obtener(id);
    }

    @PostMapping
    public int crear(@RequestBody CodigoBarrasDto dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public int actualizar(@PathVariable int id, @RequestBody CodigoBarrasDto dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public int eliminar(@PathVariable int id) {
        return service.eliminar(id);
    }
}
