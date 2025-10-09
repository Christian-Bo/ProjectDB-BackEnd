package com.nexttechstore.nexttech_backend.mapper;

import com.nexttechstore.nexttech_backend.dto.ProveedorDto;
import com.nexttechstore.nexttech_backend.model.entity.Proveedor;

/**
 * Conversión entre modelo y DTO (1:1 en snake_case).
 * Sin lógica de negocio, solo transformación de datos.
 */
public final class ProveedorMapper {

    private ProveedorMapper() {}

    public static Proveedor toEntity(ProveedorDto dto) {
        if (dto == null) return null;
        Proveedor e = new Proveedor();
        e.setId(dto.getId());
        e.setCodigo(dto.getCodigo());
        e.setNombre(dto.getNombre());
        e.setNit(dto.getNit());
        e.setTelefono(dto.getTelefono());
        e.setDireccion(dto.getDireccion());
        e.setEmail(dto.getEmail());
        e.setDias_credito(dto.getDias_credito());
        e.setContacto_principal(dto.getContacto_principal());
        e.setActivo(dto.getActivo());
        e.setFecha_registro(dto.getFecha_registro()); // normalmente lo llena BD
        e.setRegistrado_por(dto.getRegistrado_por());
        return e;
    }

    public static ProveedorDto toDto(Proveedor e) {
        if (e == null) return null;
        ProveedorDto dto = new ProveedorDto();
        dto.setId(e.getId());
        dto.setCodigo(e.getCodigo());
        dto.setNombre(e.getNombre());
        dto.setNit(e.getNit());
        dto.setTelefono(e.getTelefono());
        dto.setDireccion(e.getDireccion());
        dto.setEmail(e.getEmail());
        dto.setDias_credito(e.getDias_credito());
        dto.setContacto_principal(e.getContacto_principal());
        dto.setActivo(e.getActivo());
        dto.setFecha_registro(e.getFecha_registro());
        dto.setRegistrado_por(e.getRegistrado_por());
        return dto;
    }

    /**
     * Actualiza el entity con datos del DTO (para update).
     * No toca: id, fecha_registro.
     */
    public static void merge(Proveedor target, ProveedorDto source) {
        target.setCodigo(source.getCodigo());
        target.setNombre(source.getNombre());
        target.setNit(source.getNit());
        target.setTelefono(source.getTelefono());
        target.setDireccion(source.getDireccion());
        target.setEmail(source.getEmail());
        target.setDias_credito(source.getDias_credito());
        target.setContacto_principal(source.getContacto_principal());
        target.setActivo(source.getActivo());
        target.setRegistrado_por(source.getRegistrado_por());
    }
}
