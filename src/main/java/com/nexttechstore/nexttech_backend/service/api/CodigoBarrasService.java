package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.catalogos.CodigoBarrasDto;
import java.util.List;

public interface CodigoBarrasService {
    List<CodigoBarrasDto> listar();
    CodigoBarrasDto obtener(int id);
    int crear(CodigoBarrasDto dto);
    int actualizar(int id, CodigoBarrasDto dto);
    int eliminar(int id);
}
