package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.MarcaDto;
import java.util.List;

public interface MarcaService {
    List<MarcaDto> listar();
    MarcaDto obtener(int id);
    int crear(MarcaDto d);
    int actualizar(int id, MarcaDto d);
    int cambiarEstado(int id, int estado);
}
