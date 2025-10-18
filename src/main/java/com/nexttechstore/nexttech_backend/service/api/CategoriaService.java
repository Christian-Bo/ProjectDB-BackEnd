package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.catalogos.CategoriaDto;
import java.util.List;

public interface CategoriaService {
    List<CategoriaDto> listar();
    CategoriaDto obtener(int id);
    int crear(CategoriaDto dto);
    int actualizar(int id, CategoriaDto dto);
    int eliminar(int id);
}
