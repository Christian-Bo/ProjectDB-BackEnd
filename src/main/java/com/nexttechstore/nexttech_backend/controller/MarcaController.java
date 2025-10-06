package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.MarcaDto;
import com.nexttechstore.nexttech_backend.service.api.MarcaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    private final MarcaService service;
    public MarcaController(MarcaService service){ this.service = service; }

    @GetMapping public List<MarcaDto> listar(){ return service.listar(); }
    @GetMapping("/{id}") public MarcaDto get(@PathVariable int id){ return service.obtener(id); }
    @PostMapping public int crear(@Valid @RequestBody MarcaDto d){ return service.crear(d); }
    @PutMapping("/{id}") public int actualizar(@PathVariable int id, @Valid @RequestBody MarcaDto d){ return service.actualizar(id, d); }
    @PatchMapping("/{id}/estado/{estado}") public int estado(@PathVariable int id, @PathVariable int estado){ return service.cambiarEstado(id, estado); }
}

