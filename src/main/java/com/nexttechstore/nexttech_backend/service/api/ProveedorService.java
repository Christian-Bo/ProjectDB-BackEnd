package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.ProveedorDto;

import java.util.List;
import java.util.Map;

/**
 * Contrato de servicio para Proveedores.
 * - Se evita crear nuevos DTOs; se usa el mismo ProveedorDto.
 * - Búsqueda con paginación manual (page/size).
 * - Eliminar lógico (activo=false).
 * - Listado mínimo de empleados activos para el combo (id, nombre).
 */
public interface ProveedorService {

    ProveedorDto crear(ProveedorDto dto);

    ProveedorDto actualizar(Integer id, ProveedorDto dto);

    void eliminarLogico(Integer id);

    ProveedorDto obtenerPorId(Integer id);

    ProveedorDto obtenerPorCodigo(String codigo);

    List<ProveedorDto> buscar(String q, Boolean activo, int page, int size);

    long contar(String q, Boolean activo);

    /** Listado mínimo de empleados activos (para combo en el modal) */
    List<Map<String, Object>> listarEmpleadosActivosMin();
}
