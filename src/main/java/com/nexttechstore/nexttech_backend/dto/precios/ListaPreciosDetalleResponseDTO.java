package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaPreciosDetalleResponseDTO {
    private Integer id;
    private Integer listaId;
    private String listaNombre;
    private Integer productoId;
    private String productoCodigo;
    private String productoNombre;
    private BigDecimal precioBase;
    private BigDecimal precioLista;
    private LocalDate vigenteDesde;
    private LocalDate vigenteHasta;
    private Boolean estaVigente;
}