package com.nexttechstore.nexttech_backend.dto;

import lombok.Data;

@Data
public class VentaHeaderEditDto {
    private Integer clienteId;      // opcional
    private String  tipoPago;       // opcional: "C" o "R"
    private Integer vendedorId;     // opcional
    private Integer cajeroId;       // opcional
    private String  observaciones;  // opcional
}
