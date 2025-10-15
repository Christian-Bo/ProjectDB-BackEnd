package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface VentasService {
    int registrar(VentaRequestDto req);
    void anular(int ventaId, String motivo);
    void editarHeader(int ventaId, VentaHeaderEditDto dto);
    void editarDetalle(int ventaId, java.util.List<VentaDetalleEditItemDto> items);

    // Aún sin implementar (consulta/ listado)
    VentaDto obtenerVentaPorId(int id);
    List<VentaResumenDto> listarVentas(LocalDate desde, LocalDate hasta, Integer clienteId,
                                       String numeroVenta, Integer page, Integer size);
}
