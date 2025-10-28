package com.nexttechstore.nexttech_backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "garantias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarantiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "venta_id", nullable = false)
    private Integer ventaId;

    @Column(name = "detalle_venta_id", nullable = false)
    private Integer detalleVentaId;

    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, length = 1)
    private String estado = "V"; // V=Vigente, U=Usada, E=Expirada

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String observaciones;
}