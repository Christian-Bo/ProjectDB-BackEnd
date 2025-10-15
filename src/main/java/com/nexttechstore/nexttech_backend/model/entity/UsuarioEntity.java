package com.nexttechstore.nexttech_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios", schema = "dbo",
        uniqueConstraints = { @UniqueConstraint(columnNames = {"empleado_id"}) })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="nombre_usuario", nullable=false, unique=true, length=50)
    private String nombreUsuario;

    @Column(name="password", nullable=false, length=255)
    private String password; // BCrypt recomendado

    @Column(name="estado", nullable=false, length=1)
    private String estado; // 'A','I','B'

    @Column(name="ultimo_acceso")
    private java.time.OffsetDateTime ultimoAcceso;

    @Column(name="intentos_fallidos", nullable=false)
    private Integer intentosFallidos;

    @Column(name="fecha_creacion", nullable=false)
    private java.time.OffsetDateTime fechaCreacion;

    @Column(name="empleado_id", nullable=false)
    private Integer empleadoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="rol_id", nullable=false)
    private RolEntity rol;
}
