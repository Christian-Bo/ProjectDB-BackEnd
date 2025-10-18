package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.catalogos.ProductoDto;
import java.util.List;

public interface ProductoService {
    List<ProductoDto> listar();
    ProductoDto obtener(int id);
    int crear(ProductoDto dto);
    int actualizar(int id, ProductoDto dto);
    int eliminar(int id);
}
