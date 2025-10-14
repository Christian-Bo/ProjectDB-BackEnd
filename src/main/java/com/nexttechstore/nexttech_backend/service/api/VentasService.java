package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.VentaDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import com.nexttechstore.nexttech_backend.dto.VentaResumenDto;

import java.time.LocalDate;
import java.util.List;

public interface VentasService {
    int registrar(VentaRequestDto req);
    VentaDto obtenerVentaPorId(int id);
    void anular(int ventaId, String motivo);
    List<VentaResumenDto> listarVentas(LocalDate desde,
                                       LocalDate hasta,
                                       Integer clienteId,
                                       String numeroVenta,
                                       Integer page,
                                       Integer size);
}
