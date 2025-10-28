package com.nexttechstore.nexttech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDto {
    private Integer id;
    private Integer productoId;
    private String productoCodigo;
    private String productoNombre;
    private Integer bodegaId;
    private String bodegaNombre;
    private Integer cantidadDisponible;
    private Integer cantidadReservada;
    private Integer cantidadTransito;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private LocalDateTime ultimaActualizacion;
    private String estadoStock;
}