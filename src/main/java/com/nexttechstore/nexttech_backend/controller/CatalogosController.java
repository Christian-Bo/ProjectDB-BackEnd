package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.BodegaDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ClienteDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ProductoStockDto;
import com.nexttechstore.nexttech_backend.repository.orm.CatalogosQueryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de catálogos para poblar selects del frontend (solo lectura).
 */
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

    @GetMapping("/productos-stock")
    public List<ProductoStockDto> productosConStock() {
        return repo.productosConStock();
    }
}
