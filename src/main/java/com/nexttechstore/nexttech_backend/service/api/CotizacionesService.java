package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface CotizacionesService {
    List<CotizacionListItemDto> listar(LocalDate desde, LocalDate hasta,
                                       Integer clienteId, String numero,
                                       Integer page, Integer size);

    CotizacionDto obtenerPorId(int id);

    int crear(CotizacionCreateRequestDto req);

    int convertirAVenta(int cotizacionId, int bodegaId, int serieId, Integer cajeroId);
}
