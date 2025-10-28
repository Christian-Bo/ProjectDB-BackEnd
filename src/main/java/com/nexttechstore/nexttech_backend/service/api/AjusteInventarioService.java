package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.AjusteInventarioDto;
import java.util.List;

public interface AjusteInventarioService {
    List<AjusteInventarioDto> listarAjustes(Integer bodegaId, String tipoAjuste, String fechaDesde, String fechaHasta);
    AjusteInventarioDto obtenerAjustePorId(Integer id);
    AjusteInventarioDto crearAjuste(AjusteInventarioDto ajusteDto);
}