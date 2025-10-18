package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.ProductoDto;
import com.nexttechstore.nexttech_backend.service.api.ProductoService;
import org.springframework.web.bind.annotation.*;
import com.nexttechstore.nexttech_backend.security.AllowedRoles;

import java.util.List;

@AllowedRoles({"OPERACIONES"})
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductoDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ProductoDto obtener(@PathVariable int id) {
        return service.obtener(id);
    }

    @PostMapping
    public int crear(@RequestBody ProductoDto dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public int actualizar(@PathVariable int id, @RequestBody ProductoDto dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public int eliminar(@PathVariable int id) {
        return service.eliminar(id);
    }
}
