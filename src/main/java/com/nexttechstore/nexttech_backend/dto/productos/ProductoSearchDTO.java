package com.nexttechstore.nexttech_backend.dto.productos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoSearchDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private BigDecimal precioVenta;
    private String estado;
    private String marcaNombre;
    private String categoriaNombre;
    private String unidadMedida;
    private Integer stockTotal;
}