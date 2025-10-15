package com.nexttechstore.nexttech_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles", schema = "dbo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RolEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false, unique=true, length=50)
    private String nombre;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String descripcion;

    @Column(nullable=false)
    private Boolean activo;

    @Column(name="fecha_creacion", nullable=false)
    private java.time.OffsetDateTime fechaCreacion;
}
