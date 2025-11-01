package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.BodegaDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ClienteDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.ProductoStockDto;
import com.nexttechstore.nexttech_backend.dto.catalogos.EmpleadoDto;
import com.nexttechstore.nexttech_backend.repository.orm.CatalogosQueryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
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

    // ✅ acepta ?bodegaId= para filtrar por bodega; si no se envía, devuelve stock total
    @GetMapping("/productos-stock")
    public List<ProductoStockDto> productosConStock(@RequestParam(required = false) Integer bodegaId) {
        return repo.productosConStock(bodegaId);
    }

    // ✅ catálogo de empleados (para Vendedor y Cajero)
    @GetMapping("/empleados")
    public List<EmpleadoDto> empleados() {
        return repo.empleados();
    }

    @GetMapping("/series-facturas")
    public java.util.List<java.util.Map<String,Object>> seriesFacturas(JdbcTemplate jdbc) {
        return jdbc.queryForList("""
        SELECT id, serie, tipo_documento, correlativo_actual, correlativo_maximo, estado
        FROM series_facturas
        WHERE estado='A'
        ORDER BY serie
    """);
    }
}
