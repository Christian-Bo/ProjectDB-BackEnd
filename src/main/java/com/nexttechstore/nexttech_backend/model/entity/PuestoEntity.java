package com.nexttechstore.nexttech_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "puestos", schema = "dbo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PuestoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false, length=100)
    private String nombre;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String descripcion;

    @Column(nullable=false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="departamento_id", nullable=false)
    private DepartamentoEntity departamento;

    @Column(name="fecha_creacion", nullable=false)
    private OffsetDateTime fechaCreacion;
}
