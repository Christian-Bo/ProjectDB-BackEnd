package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.ProveedorDto;
import com.nexttechstore.nexttech_backend.mapper.ProveedorMapper;
import com.nexttechstore.nexttech_backend.model.entity.Proveedor;
import com.nexttechstore.nexttech_backend.repository.orm.ProveedorRepository;
import com.nexttechstore.nexttech_backend.service.api.ProveedorService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository repo;

    public ProveedorServiceImpl(ProveedorRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProveedorDto crear(ProveedorDto dto) {
        // 1) Unicidad de código
        if (repo.existsByCodigo(dto.getCodigo())) {
            throw new DataIntegrityViolationException("El código ya existe: " + dto.getCodigo());
        }
        // 2) FK: empleado registrador debe existir
        if (!repo.existsEmpleadoById(dto.getRegistrado_por())) {
            throw new DataIntegrityViolationException(
                    "El 'registrado_por' (" + dto.getRegistrado_por() + ") no existe en empleados."
            );
        }

        Proveedor entity = ProveedorMapper.toEntity(dto);
        try {
            Proveedor saved = repo.save(entity);
            return ProveedorMapper.toDto(saved);
        } catch (DataIntegrityViolationException ex) {
            // Por si la BD lanza el FK igualmente (carrera, etc.), traducimos el mensaje:
            String msg = ex.getMessage() != null ? ex.getMessage() : "";
            if (msg.contains("FK_proveedores_registrador")) {
                throw new DataIntegrityViolationException(
                        "Violación de llave foránea: 'registrado_por' no existe en empleados."
                );
            }
            throw ex;
        }
    }

    @Override
    public ProveedorDto actualizar(Integer id, ProveedorDto dto) {
        Proveedor actual = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado: id=" + id));

        // 1) Unicidad si cambiaron el código
        if (!actual.getCodigo().equals(dto.getCodigo()) && repo.existsByCodigo(dto.getCodigo())) {
            throw new DataIntegrityViolationException("El código ya existe: " + dto.getCodigo());
        }
        // 2) FK registrador (por si lo cambian)
        if (!repo.existsEmpleadoById(dto.getRegistrado_por())) {
            throw new DataIntegrityViolationException(
                    "El 'registrado_por' (" + dto.getRegistrado_por() + ") no existe en empleados."
            );
        }

        ProveedorMapper.merge(actual, dto);
        try {
            Proveedor updated = repo.update(actual);
            return ProveedorMapper.toDto(updated);
        } catch (DataIntegrityViolationException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";
            if (msg.contains("FK_proveedores_registrador")) {
                throw new DataIntegrityViolationException(
                        "Violación de llave foránea: 'registrado_por' no existe en empleados."
                );
            }
            throw ex;
        }
    }

    @Override
    public void eliminarLogico(Integer id) {
        repo.findById(id).orElseThrow(() ->
                new NoSuchElementException("Proveedor no encontrado: id=" + id));
        repo.deleteLogical(id);
    }

    @Override
    public ProveedorDto obtenerPorId(Integer id) {
        return repo.findById(id)
                .map(ProveedorMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado: id=" + id));
    }

    @Override
    public ProveedorDto obtenerPorCodigo(String codigo) {
        return repo.findByCodigo(codigo)
                .map(ProveedorMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado: codigo=" + codigo));
    }

    @Override
    public List<ProveedorDto> buscar(String q, Boolean activo, int page, int size) {
        return repo.search(q, activo, page, size).stream()
                .map(ProveedorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public long contar(String q, Boolean activo) {
        return repo.countSearch(q, activo);
    }
}
