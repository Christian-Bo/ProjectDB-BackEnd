package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.MarcaDto;
import com.nexttechstore.nexttech_backend.mapper.MarcaMapper;
import com.nexttechstore.nexttech_backend.model.entity.MarcaEntity;
import com.nexttechstore.nexttech_backend.repository.orm.MarcaJpaRepository;
import com.nexttechstore.nexttech_backend.service.api.MarcaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class MarcaServiceImpl implements MarcaService {

    private final MarcaJpaRepository repo;
    private final MarcaMapper mapper;

    public MarcaServiceImpl(MarcaJpaRepository repo, MarcaMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarcaDto> listar() {
        return repo.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MarcaDto obtener(int id) {
        var e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Marca no encontrada: " + id));
        return mapper.toDto(e);
    }

    @Override
    public int crear(MarcaDto dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");

        if (repo.existsByNombreIgnoreCase(dto.getNombre()))
            throw new IllegalArgumentException("Ya existe una marca con ese nombre.");

        MarcaEntity e = mapper.toEntity(dto);
        if (e.getFechaCreacion() == null) e.setFechaCreacion(OffsetDateTime.now());
        if (e.getActivo() == null) e.setActivo(Boolean.TRUE);

        e = repo.save(e);
        return e.getId();
    }

    @Override
    public int actualizar(int id, MarcaDto dto) {
        var e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Marca no encontrada: " + id));

        if (dto.getNombre() != null && !dto.getNombre().isBlank()
                && !dto.getNombre().equalsIgnoreCase(e.getNombre())
                && repo.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe una marca con ese nombre.");
        }

        mapper.updateEntityFromDto(dto, e);
        repo.save(e);
        return 1;
    }

    @Override
    public int cambiarEstado(int id, int estado) {
        var e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Marca no encontrada: " + id));
        e.setActivo(estado == 1);
        repo.save(e);
        return 1;
    }
}
