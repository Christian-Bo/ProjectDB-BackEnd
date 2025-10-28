package com.nexttechstore.nexttech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleTransferenciaDto {
    private Integer id;
    private Integer transferenciaId;
    private Integer productoId;
    private String productoCodigo;
    private String productoNombre;
    private Integer cantidadSolicitada;
    private Integer cantidadAprobada;
    private Integer cantidadRecibida;
    private String observaciones;
}