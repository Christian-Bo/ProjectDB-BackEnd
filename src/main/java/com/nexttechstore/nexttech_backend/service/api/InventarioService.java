package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.AlertaInventarioDto;
import com.nexttechstore.nexttech_backend.dto.InventarioDto;
import java.util.List;

public interface InventarioService {
    List<InventarioDto> listarInventario(Integer bodegaId);
    List<InventarioDto> inventarioPorProducto(Integer productoId);
    List<InventarioDto> inventarioPorBodega(Integer bodegaId);
    List<InventarioDto> listarKardex(Integer productoId, Integer bodegaId, String fechaDesde, String fechaHasta);
    List<AlertaInventarioDto> listarAlertas(Integer bodegaId);
}