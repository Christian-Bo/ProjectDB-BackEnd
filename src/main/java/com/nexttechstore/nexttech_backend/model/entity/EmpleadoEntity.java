package com.nexttechstore.nexttech_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "empleados", schema = "dbo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmpleadoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false, unique=true, length=20)
    private String codigo;

    @Column(nullable=false, length=100)
    private String nombres;

    @Column(nullable=false, length=100)
    private String apellidos;

    @Column(nullable=false, unique=true, length=13)
    private String dpi;

    @Column(length=15)
    private String nit;

    @Column(length=20)
    private String telefono;

    @Column(length=100)
    private String email;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String direccion;

    private LocalDate fechaNacimiento;

    @Column(nullable=false)
    private LocalDate fechaIngreso;

    private LocalDate fechaSalida;

    /** 'A' (Activo), 'I' (Inactivo), 'S' (Suspendido) */
    @Column(nullable=false, length=1)
    private String estado = "A";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="puesto_id", nullable=false)
    private PuestoEntity puesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="jefe_inmediato_id")
    private EmpleadoEntity jefeInmediato;

    @Column(length=255)
    private String foto;

    @Column(name="fecha_creacion", nullable=false)
    private OffsetDateTime fechaCreacion;
}
