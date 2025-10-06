package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.model.Producto;
import java.util.List;

public interface ProductoService {
    List<Producto> listar();
    // Listar con filtro (texto/marca/categoría) — mapea al SP sp_productos_listar_filtro
    List<Producto> listar(String texto, Integer marcaId, Integer categoriaId);

    Producto obtener(int id);
    int crear(Producto p);
    int actualizar(int id, Producto p);
    int eliminar(int id);                // lógico
    int cambiarEstado(int id, int estado);
}
