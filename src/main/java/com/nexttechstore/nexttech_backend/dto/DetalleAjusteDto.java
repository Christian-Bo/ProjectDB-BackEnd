package com.nexttechstore.nexttech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleAjusteDto {
    private Integer id;
    private Integer ajusteId;
    private Integer productoId;
    private String productoCodigo;
    private String productoNombre;
    private Integer cantidadAntes;
    private Integer cantidadAjuste;
    private Integer cantidadDespues;
    private BigDecimal costoUnitario;
    private String observaciones;
}