package com.nexttechstore.nexttech_backend.model.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Modelo simple (sin JPA) alineado 1:1 con columnas de la tabla [proveedores].
 * Solo @Data, @NotBlank y @NotNull, como acordamos.
 *
 * Columnas de BD:
 *  id, codigo, nombre, nit, telefono, direccion, email, dias_credito,
 *  contacto_principal, activo, fecha_registro, registrado_por
 */
@Data
public class Proveedor {

    /** [id] PK autoincremental (lo asigna la BD). */
    private Integer id;

    /** [codigo] requerido y único a nivel de negocio. */
    @NotBlank(message = "El código es obligatorio.")
    private String codigo;

    /** [nombre] requerido. */
    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    /** [nit] requerido para facturación. */
    @NotBlank(message = "El NIT es obligatorio.")
    private String nit;

    /** [telefono] requerido para contacto. */
    @NotBlank(message = "El teléfono es obligatorio.")
    private String telefono;

    /** [direccion] opcional. */
    private String direccion;

    /** [email] opcional. */
    private String email;

    /** [dias_credito] requerido (>=0 validado en capa SQL o negocio). */
    @NotNull(message = "Los días de crédito son obligatorios.")
    private Integer dias_credito;

    /** [contacto_principal] opcional. */
    private String contacto_principal;

    /** [activo] requerido (1/0). */
    @NotNull(message = "El estado 'activo' es obligatorio.")
    private Boolean activo;

    /** [fecha_registro] (la establece la BD). */
    private OffsetDateTime fecha_registro;

    /** [registrado_por] requerido (usuario/empleado). */
    @NotNull(message = "El campo 'registrado_por' es obligatorio.")
    private Integer registrado_por;
}
