package com.nexttechstore.nexttech_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "departamentos", schema = "dbo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DepartamentoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false, unique=true, length=100)
    private String nombre;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String descripcion;

    @Column(nullable=false)
    private Boolean activo = true;

    @Column(name="fecha_creacion", nullable=false)
    private OffsetDateTime fechaCreacion;
}
