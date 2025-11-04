package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.facturas.FacturaHeaderDto;
import com.nexttechstore.nexttech_backend.dto.facturas.FacturaDetalleDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FacturasService {
    int emitir(int ventaId, int serieId, int emitidaPor);

    Map<String, Object> listar(LocalDate desde, LocalDate hasta, String serie, String numero, int page, int size);

    Map<String, Object> obtenerPorId(int id);

    byte[] generarPdf(int id);
}
