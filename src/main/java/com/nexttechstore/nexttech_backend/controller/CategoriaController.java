package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.CategoriaDto;
import com.nexttechstore.nexttech_backend.service.api.CategoriaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoriaDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public CategoriaDto obtener(@PathVariable int id) {
        return service.obtener(id);
    }

    @PostMapping
    public int crear(@RequestBody CategoriaDto dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public int actualizar(@PathVariable int id, @RequestBody CategoriaDto dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public int eliminar(@PathVariable int id) {
        return service.eliminar(id);
    }
}
