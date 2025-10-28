package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.productos.*;
import com.nexttechstore.nexttech_backend.dto.common.*;
import com.nexttechstore.nexttech_backend.repository.sp.ProductosSpRepository;
import com.nexttechstore.nexttech_backend.service.api.ProductosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductosServiceImpl implements ProductosService {

    private final ProductosSpRepository productosRepository;

    @Override
    @Transactional
    public ApiResponse<ProductoResponseDTO> create(ProductoRequestDTO request) {
        log.info("Creando producto: {}", request.getNombre());

        // Validaciones de negocio
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            return ApiResponse.error("El nombre del producto es requerido");
        }

        if (request.getPrecioCompra() == null || request.getPrecioCompra().doubleValue() <= 0) {
            return ApiResponse.error("El precio de compra debe ser mayor a cero");
        }

        if (request.getCreadoPor() == null) {
            return ApiResponse.error("El empleado creador es requerido");
        }

        // Validar stock
        if (request.getStockMaximo() > 0 && request.getStockMinimo() > request.getStockMaximo()) {
            return ApiResponse.error("El stock mínimo no puede ser mayor al stock máximo");
        }

        return productosRepository.create(request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> update(Integer id, ProductoRequestDTO request) {
        log.info("Actualizando producto ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("ID de producto inválido");
        }

        // Validaciones similares a create
        if (request.getStockMaximo() > 0 && request.getStockMinimo() > request.getStockMaximo()) {
            return ApiResponse.error("El stock mínimo no puede ser mayor al stock máximo");
        }

        return productosRepository.update(id, request);
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(Integer id) {
        log.info("Eliminando producto ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("ID de producto inválido");
        }

        return productosRepository.delete(id);
    }

    @Override
    @Transactional
    public ApiResponse<Void> activate(Integer id) {
        log.info("Activando producto ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("ID de producto inválido");
        }

        return productosRepository.activate(id);
    }

    @Override
    @Transactional
    public ApiResponse<Void> descontinuar(Integer id, String motivo) {
        log.info("Descontinuando producto ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("ID de producto inválido");
        }

        return productosRepository.descontinuar(id, motivo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoResponseDTO> getById(Integer id) {
        log.info("Obteniendo producto ID: {}", id);

        if (id == null || id <= 0) {
            return Optional.empty();
        }

        return productosRepository.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ProductoResponseDTO>> getAll(Boolean soloActivos, Integer marcaId, Integer categoriaId, PageRequest pageRequest) {
        log.info("Obteniendo todos los productos. Marca: {}, Categoría: {}", marcaId, categoriaId);
        return productosRepository.getAll(
                soloActivos != null ? soloActivos : true,
                marcaId,
                categoriaId,
                pageRequest
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ProductoSearchDTO>> search(String criterio, Boolean soloActivos) {
        log.info("Buscando productos con criterio: {}", criterio);

        if (criterio == null || criterio.trim().isEmpty()) {
            return ApiResponse.error("El criterio de búsqueda no puede estar vacío");
        }

        return productosRepository.search(criterio, soloActivos != null ? soloActivos : true);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoSearchDTO> buscarPorCodigoBarras(String codigoBarras) {
        log.info("Buscando producto por código de barras: {}", codigoBarras);

        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            return Optional.empty();
        }

        return productosRepository.buscarPorCodigoBarras(codigoBarras);
    }
}