package com.nexttechstore.nexttech_backend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "marcas")
@Getter @Setter
public class MarcaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false, length=120)
    private String nombre;

    @Column(nullable=false)
    private Integer estado; // 1=activo,0=inactivo
}
