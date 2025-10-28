package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaPreciosResponseDTO {
    private Integer id;
    private String nombre;
    private String moneda;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
    private Integer totalProductos;
    private Integer totalClientes;
}
