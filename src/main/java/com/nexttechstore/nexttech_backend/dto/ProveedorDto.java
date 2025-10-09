package com.nexttechstore.nexttech_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * DTO único para Proveedor (entrada/salida).
 * Campos en snake_case para coincidir con la BD y simplificar mapeo.
 */
@Data
public class ProveedorDto {

    private Integer id;

    @NotBlank(message = "El código es obligatorio.")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @NotBlank(message = "El NIT es obligatorio.")
    private String nit;

    @NotBlank(message = "El teléfono es obligatorio.")
    private String telefono;

    // Opcionales
    private String direccion;
    private String email;
    private String contacto_principal;

    @NotNull(message = "Los días de crédito son obligatorios.")
    private Integer dias_credito;

    @NotNull(message = "El estado 'activo' es obligatorio.")
    private Boolean activo;

    // Solo lectura
    private OffsetDateTime fecha_registro;

    @NotNull(message = "El campo 'registrado_por' es obligatorio.")
    private Integer registrado_por;
}
