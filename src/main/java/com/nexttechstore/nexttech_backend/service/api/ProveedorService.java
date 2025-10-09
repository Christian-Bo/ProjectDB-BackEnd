package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.ProveedorDto;

import java.util.List;

/**
 * Servicio de orquestación para Proveedor.
 * Reusa exceptions generales del proyecto.
 */
public interface ProveedorService {

    ProveedorDto crear(ProveedorDto dto);

    ProveedorDto actualizar(Integer id, ProveedorDto dto);

    void eliminarLogico(Integer id);

    ProveedorDto obtenerPorId(Integer id);

    ProveedorDto obtenerPorCodigo(String codigo);

    List<ProveedorDto> buscar(String q, Boolean activo, int page, int size);

    long contar(String q, Boolean activo);
}
