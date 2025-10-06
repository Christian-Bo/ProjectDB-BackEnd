package com.nexttechstore.nexttech_backend.mapper;

import com.nexttechstore.nexttech_backend.dto.MarcaDto;
import com.nexttechstore.nexttech_backend.model.entity.MarcaEntity;

public class MarcaMapper {
    public static MarcaDto toDto(MarcaEntity e){
        if (e==null) return null;
        MarcaDto d = new MarcaDto();
        d.setId(e.getId());
        d.setNombre(e.getNombre());
        d.setEstado(e.getEstado());
        return d;
    }
    public static void copyToEntity(MarcaDto d, MarcaEntity e){
        e.setNombre(d.getNombre());
        e.setEstado(d.getEstado());
    }
}

