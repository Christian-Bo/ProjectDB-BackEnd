package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.MarcaDto;
import com.nexttechstore.nexttech_backend.exception.BadRequestException;
import com.nexttechstore.nexttech_backend.exception.ResourceNotFoundException;
import com.nexttechstore.nexttech_backend.mapper.MarcaMapper;
import com.nexttechstore.nexttech_backend.model.entity.MarcaEntity;
import com.nexttechstore.nexttech_backend.repository.orm.MarcaJpaRepository;
import com.nexttechstore.nexttech_backend.service.api.MarcaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarcaServiceImpl implements MarcaService {

    private final MarcaJpaRepository repo;

    public MarcaServiceImpl(MarcaJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<MarcaDto> listar() {
        return repo.findAll().stream().map(MarcaMapper::toDto).toList();
    }

    @Override
    public MarcaDto obtener(int id) {
        var e = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada: " + id));
        return MarcaMapper.toDto(e);
    }

    @Override @Transactional
    public int crear(MarcaDto d) {
        if (repo.existsByNombreIgnoreCase(d.getNombre()))
            throw new BadRequestException("La marca ya existe");
        MarcaEntity e = new MarcaEntity();
        MarcaMapper.copyToEntity(d, e);
        return repo.save(e).getId();
    }

    @Override @Transactional
    public int actualizar(int id, MarcaDto d) {
        var e = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));
        if (!e.getNombre().equalsIgnoreCase(d.getNombre()) && repo.existsByNombreIgnoreCase(d.getNombre()))
            throw new BadRequestException("La marca ya existe");
        MarcaMapper.copyToEntity(d, e);
        repo.save(e);
        return 1;
    }

    @Override @Transactional
    public int cambiarEstado(int id, int estado) {
        var e = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));
        e.setEstado(estado);
        repo.save(e);
        return 1;
    }
}
