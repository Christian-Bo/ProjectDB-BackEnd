package com.nexttechstore.nexttech_backend.mapper;

import com.nexttechstore.nexttech_backend.dto.rrhh.*;
import com.nexttechstore.nexttech_backend.model.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RrhhMapper {

    // Departamento
    DepartamentoDto toDto(DepartamentoEntity e);

    // Puesto
    @Mapping(source = "departamento.id", target = "departamentoId")
    @Mapping(source = "departamento.nombre", target = "departamentoNombre")
    PuestoDto toDto(PuestoEntity e);

    // Empleado
    @Mapping(source = "puesto.id", target = "puestoId")
    @Mapping(source = "puesto.nombre", target = "puestoNombre")
    @Mapping(source = "puesto.departamento.id", target = "departamentoId")
    @Mapping(source = "puesto.departamento.nombre", target = "departamentoNombre")
    @Mapping(source = "jefeInmediato.id", target = "jefeId")
    @Mapping(expression = "java(e.getJefeInmediato()!=null ? e.getJefeInmediato().getNombres()+\" \"+e.getJefeInmediato().getApellidos() : null)", target="jefeNombreCompleto")
    EmpleadoDto toDto(EmpleadoEntity e);
}
