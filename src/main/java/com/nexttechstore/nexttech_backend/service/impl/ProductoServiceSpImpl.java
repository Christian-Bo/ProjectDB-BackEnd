package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.exception.ResourceNotFoundException;
import com.nexttechstore.nexttech_backend.model.Producto;
import com.nexttechstore.nexttech_backend.repository.sp.ProductoSpRepository; // <-- CORREGIDO
import com.nexttechstore.nexttech_backend.service.api.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoServiceSpImpl implements ProductoService {

    private final ProductoSpRepository repo; // <-- CORREGIDO

    public ProductoServiceSpImpl(ProductoSpRepository repo) { // <-- CORREGIDO
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return repo.listarProductos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listar(String texto, Integer marcaId, Integer categoriaId) {
        return repo.listarProductosConFiltro(texto, marcaId, categoriaId);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtener(int id) {
        Producto p = repo.obtenerProductoPorId(id);
        if (p == null) {
            throw new ResourceNotFoundException("Producto no encontrado: " + id);
        }
        return p;
    }

    @Override
    @Transactional
    public int crear(Producto p) {
        Integer outId = repo.crearProducto(p);
        if (outId == null) {
            throw new IllegalStateException("No se pudo crear el producto (OutDocumentoId nulo).");
        }
        return outId;
    }

    @Override
    @Transactional
    public int actualizar(int id, Producto p) {
        Integer outId = repo.actualizarProducto(id, p);
        if (outId == null) {
            throw new IllegalStateException("No se pudo actualizar el producto ID " + id + " (OutDocumentoId nulo).");
        }
        return outId;
    }

    @Override
    @Transactional
    public int eliminar(int id) {
        Integer outId = repo.eliminarProductoLogico(id);
        if (outId == null) {
            throw new IllegalStateException("No se pudo eliminar lógicamente el producto ID " + id + " (OutDocumentoId nulo).");
        }
        return outId;
    }

    @Override
    @Transactional
    public int cambiarEstado(int id, int estado) {
        Integer outId = repo.cambiarEstadoProducto(id, estado);
        if (outId == null) {
            throw new IllegalStateException("No se pudo cambiar el estado del producto ID " + id + " (OutDocumentoId nulo).");
        }
        return outId;
    }
}
