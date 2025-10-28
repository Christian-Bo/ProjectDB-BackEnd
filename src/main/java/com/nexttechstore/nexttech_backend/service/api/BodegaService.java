package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.BodegaDto;
import java.util.List;

public interface BodegaService {
    List<BodegaDto> listarBodegas();
    BodegaDto obtenerBodegaPorId(Integer id);
    BodegaDto crearBodega(BodegaDto bodegaDto);
    BodegaDto actualizarBodega(Integer id, BodegaDto bodegaDto);
    void eliminarBodega(Integer id);
}