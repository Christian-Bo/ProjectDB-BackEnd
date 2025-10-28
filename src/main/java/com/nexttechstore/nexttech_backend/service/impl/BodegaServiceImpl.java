package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.BodegaDto;
import com.nexttechstore.nexttech_backend.repository.sp.BodegaSpRepository;
import com.nexttechstore.nexttech_backend.service.api.BodegaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BodegaServiceImpl implements BodegaService {

    @Autowired
    private BodegaSpRepository bodegaSpRepository;

    @Override
    public List<BodegaDto> listarBodegas() {
        List<Map<String, Object>> result = bodegaSpRepository.listarBodegas();
        List<BodegaDto> bodegas = new ArrayList<>();

        for (Map<String, Object> row : result) {
            BodegaDto dto = mapearBodega(row);
            bodegas.add(dto);
        }

        return bodegas;
    }

    @Override
    public BodegaDto obtenerBodegaPorId(Integer id) {
        Map<String, Object> row = bodegaSpRepository.obtenerBodegaPorId(id);
        return row != null ? mapearBodega(row) : null;
    }

    @Override
    public BodegaDto crearBodega(BodegaDto bodegaDto) {
        Map<String, Object> result = bodegaSpRepository.crearBodega(
                bodegaDto.getCodigo(),
                bodegaDto.getNombre(),
                bodegaDto.getUbicacion(),
                bodegaDto.getCapacidadMaxima(),
                bodegaDto.getResponsableId(),
                bodegaDto.getTelefono(),
                bodegaDto.getEmail()
        );

        if (result != null && result.containsKey("id")) {
            Integer newId = ((Number) result.get("id")).intValue();
            return obtenerBodegaPorId(newId);
        }

        return null;
    }

    @Override
    public BodegaDto actualizarBodega(Integer id, BodegaDto bodegaDto) {
        bodegaSpRepository.actualizarBodega(
                id,
                bodegaDto.getCodigo(),
                bodegaDto.getNombre(),
                bodegaDto.getUbicacion(),
                bodegaDto.getCapacidadMaxima(),
                bodegaDto.getResponsableId(),
                bodegaDto.getTelefono(),
                bodegaDto.getEmail()
        );

        return obtenerBodegaPorId(id);
    }

    @Override
    public void eliminarBodega(Integer id) {
        bodegaSpRepository.eliminarBodega(id);
    }

    private BodegaDto mapearBodega(Map<String, Object> row) {
        BodegaDto dto = new BodegaDto();
        dto.setId((Integer) row.get("id"));
        dto.setCodigo((String) row.get("codigo"));
        dto.setNombre((String) row.get("nombre"));
        dto.setUbicacion((String) row.get("ubicacion"));
        dto.setCapacidadMaxima((Integer) row.get("capacidad_maxima"));
        dto.setResponsableId((Integer) row.get("responsable_id"));
        dto.setTelefono((String) row.get("telefono"));
        dto.setEmail((String) row.get("email"));
        dto.setActivo((Boolean) row.get("activo"));

        Object fechaCreacion = row.get("fecha_creacion");
        if (fechaCreacion != null) {
            dto.setFechaCreacion(LocalDateTime.parse(fechaCreacion.toString().replace(" ", "T")));
        }

        return dto;
    }
}