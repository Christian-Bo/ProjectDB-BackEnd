package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.model.Producto;
import com.nexttechstore.nexttech_backend.service.api.ProductoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.nexttechstore.nexttech_backend.security.AllowedRoles;

import java.util.List;

@AllowedRoles({"OPERACIONES"})
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;
    public ProductoController(ProductoService service){ this.service = service; }

    @GetMapping
    public List<Producto> listar(){
        return service.listar();
    }

    // Endpoint para filtros: /api/productos/buscar?texto=xxx&marcaId=1&categoriaId=2
    @GetMapping("/buscar")
    public List<Producto> listarConFiltro(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Integer marcaId,
            @RequestParam(required = false) Integer categoriaId
    ){
        return service.listar(texto, marcaId, categoriaId);
    }

    @GetMapping("/{id}")
    public Producto get(@PathVariable int id){
        return service.obtener(id);
    }

    @PostMapping
    public int crear(@Valid @RequestBody Producto p){
        return service.crear(p);
    }

    @PutMapping("/{id}")
    public int actualizar(@PathVariable int id, @Valid @RequestBody Producto p){
        return service.actualizar(id, p);
    }

    @DeleteMapping("/{id}")
    public int eliminar(@PathVariable int id){
        return service.eliminar(id); // lógico
    }

    @PatchMapping("/{id}/estado/{estado}")
    public int estado(@PathVariable int id, @PathVariable int estado){
        return service.cambiarEstado(id, estado);
    }
}
