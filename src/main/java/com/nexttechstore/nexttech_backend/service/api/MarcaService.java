package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.MarcaDto;
import java.util.List;

public interface MarcaService {
    List<MarcaDto> listar();
    MarcaDto obtener(int id);
    int crear(MarcaDto dto);              // retorna id creado
    int actualizar(int id, MarcaDto dto); // retorna 1 si ok
    int cambiarEstado(int id, int estado);
}
