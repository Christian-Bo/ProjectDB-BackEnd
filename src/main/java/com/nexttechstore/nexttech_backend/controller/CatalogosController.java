package com.nexttechstore.nexttech_backend.controller;

import com.nexttechstore.nexttech_backend.dto.catalogos.*;
import com.nexttechstore.nexttech_backend.repository.orm.CatalogosQueryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    // ================== NUEVOS ENDPOINTS PARA DEVOLUCIONES ==================

    /** Llenar el <select> de ventas por número (máximo 1..200). */
    @GetMapping("/ventas-lite")
    public List<VentaLiteDto> ventasLite(@RequestParam(defaultValue = "50") int max) {
        int safeMax = Math.max(1, Math.min(200, max));
        return repo.ventasLite(safeMax);
    }

    /** Traer líneas de una venta para pre-cargar el maestro/detalle del modal. */
    @GetMapping("/venta/{ventaId}/detalle-lite")
    public List<DetalleVentaLiteDto> detalleVentaLite(@PathVariable int ventaId) {
        return repo.detalleVentaLite(ventaId);
    }

    // ======= NUEVO: productos-lite para tu modal =======
    @GetMapping("/productos-lite")
    public List<ProductoLiteDto> productosLite(
            @RequestParam(defaultValue = "") String texto,
            @RequestParam(defaultValue = "50") int max
    ){
        return repo.productosLite(texto, max);
    }

}
