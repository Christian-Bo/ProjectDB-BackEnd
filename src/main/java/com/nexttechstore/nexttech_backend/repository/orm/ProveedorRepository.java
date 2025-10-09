package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.model.entity.Proveedor;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository {

    Proveedor save(Proveedor p) throws DataIntegrityViolationException;

    Proveedor update(Proveedor p) throws DataIntegrityViolationException;

    Optional<Proveedor> findById(Integer id);

    Optional<Proveedor> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    void deleteLogical(Integer id); // activo=0

    List<Proveedor> search(String query, Boolean activo, int page, int size);

    long countSearch(String query, Boolean activo);

    /** Nuevo: valida existencia del empleado que registra. */
    boolean existsEmpleadoById(Integer empleadoId);
}
