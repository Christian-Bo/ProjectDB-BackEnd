package com.nexttechstore.nexttech_backend.mapper;

import com.nexttechstore.nexttech_backend.dto.MarcaDto;
import com.nexttechstore.nexttech_backend.model.entity.MarcaEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MarcaMapper {

    // Entity -> DTO
    @Mapping(target = "estado", expression = "java( entity.getActivo() != null && entity.getActivo() ? 1 : 0 )")
    MarcaDto toDto(MarcaEntity entity);

    // DTO -> Entity (crear)
    @Mapping(target = "activo", expression = "java( dto.getEstado() != null ? dto.getEstado() == 1 : true )")
    MarcaEntity toEntity(MarcaDto dto);

    // DTO (patch) -> Entity (actualizar)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "activo", expression =
            "java( dto.getEstado() == null ? target.getActivo() : dto.getEstado() == 1 )")
    void updateEntityFromDto(MarcaDto dto, @MappingTarget MarcaEntity target);
}
