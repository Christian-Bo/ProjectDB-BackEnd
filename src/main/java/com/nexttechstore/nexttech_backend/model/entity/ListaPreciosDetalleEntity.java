package com.nexttechstore.nexttech_backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "listas_precios_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaPreciosDetalleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lista_id", nullable = false)
    private Integer listaId;

    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal precio;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;
}
