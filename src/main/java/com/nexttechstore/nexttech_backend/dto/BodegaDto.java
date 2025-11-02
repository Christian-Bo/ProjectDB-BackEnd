package com.nexttechstore.nexttech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodegaDto {
    private Integer id;
    private String codigo;
    private String nombre;
    private String direccion;
    private Integer capacidadMaxima;
    private Integer responsableId;
    private String responsableNombre;
    private String telefono;
    private String email;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}