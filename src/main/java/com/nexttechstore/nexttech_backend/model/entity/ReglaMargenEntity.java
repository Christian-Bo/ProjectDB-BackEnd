package com.nexttechstore.nexttech_backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "reglas_margen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaMargenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "categoria_id")
    private Integer categoriaId;

    @Column(name = "marca_id")
    private Integer marcaId;

    @Column(name = "margen_pct", nullable = false, precision = 9, scale = 4)
    private BigDecimal margenPct;
}