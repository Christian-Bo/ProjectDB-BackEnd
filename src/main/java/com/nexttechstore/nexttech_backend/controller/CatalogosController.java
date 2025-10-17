package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.BodegaDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ClienteDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ProductoStockDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.EmpleadoDto;
import com.nexttechstore.nexttech_backend.repository.orm.CatalogosQueryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogosController {

    private final CatalogosQueryRepository repo;

    public CatalogosController(CatalogosQueryRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/series")
    public List<CatalogosQueryRepository.SerieItem> seriesFactura() {
        return repo.seriesFactura();
    }

    @GetMapping("/clientes")
    public List<ClienteDto> clientes() {
        return repo.clientes();
    }

    @GetMapping("/bodegas")
    public List<BodegaDto> bodegas() {
        return repo.bodegas();
    }

    // ✅ ahora acepta ?bodegaId= para filtrar por bodega
    @GetMapping("/productos-stock")
    public List<ProductoStockDto> productosConStock(@RequestParam(required = false) Integer bodegaId) {
        return repo.productosConStock(bodegaId);
    }

    // ✅ catálogo de empleados (para Vendedor y Cajero)
    @GetMapping("/empleados")
    public List<EmpleadoDto> empleados() {
        return repo.empleados();
    }
}
