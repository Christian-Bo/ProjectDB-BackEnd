package com.nexttechstore.nexttech_backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;          // <-- IMPORTANTE

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String descripcion;

    @Column(name = "precio_compra", precision = 19, scale = 4)
    private BigDecimal precioCompra;

    @Column(name = "precio_venta", precision = 19, scale = 4)
    private BigDecimal precioVenta;

    @Default
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    @Default
    @Column(name = "stock_maximo", nullable = false)
    private Integer stockMaximo = 0;

    @Default
    @Column(nullable = false, length = 1)
    private String estado = "A"; // A=Activo, I=Inactivo, D=Descontinuado

    @Column(name = "marca_id")
    private Integer marcaId;

    @Column(name = "categoria_id")
    private Integer categoriaId;

    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    @Default
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida = "UNIDAD";

    @Column(precision = 8, scale = 3)
    private BigDecimal peso;

    @Default
    @Column(name = "garantia_meses", nullable = false)
    private Integer garantiaMeses = 0;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "creado_por", nullable = false)
    private Integer creadoPor;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (stockMinimo == null)   stockMinimo = 0;
        if (stockMaximo == null)   stockMaximo = 0;
        if (estado == null)        estado = "A";
        if (unidadMedida == null)  unidadMedida = "UNIDAD";
        if (garantiaMeses == null) garantiaMeses = 0;
        if (creadoPor == null)     creadoPor = 0; // ajústalo si tu flujo siempre envía el usuario creador
    }
}
