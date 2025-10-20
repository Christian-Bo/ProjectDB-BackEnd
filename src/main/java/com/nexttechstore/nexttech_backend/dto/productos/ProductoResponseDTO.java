package com.nexttechstore.nexttech_backend.dto.productos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private String estado;
    private String estadoNombre;
    private Integer marcaId;
    private String marcaNombre;
    private Integer categoriaId;
    private String categoriaNombre;
    private String codigoBarras;
    private String unidadMedida;
    private BigDecimal peso;
    private Integer garantiaMeses;
    private LocalDateTime fechaCreacion;
    private Integer creadoPor;
    private String creadoPorNombre;
    private Integer stockTotal;
    private BigDecimal margenActualPct;
}