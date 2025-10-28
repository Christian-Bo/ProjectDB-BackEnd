package com.nexttechstore.nexttech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaInventarioDto {
    private Integer id;
    private Integer productoId;
    private String productoCodigo;
    private String productoNombre;
    private Integer bodegaId;
    private String bodegaNombre;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer stockActual;
    private String tipoAlerta;
    private Boolean activo;
}