package com.nexttechstore.nexttech_backend.dto.catalogos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoriaDto {
    private int id;
    private String nombre;
    private String descripcion;
    private Integer categoriaPadreId;
    private String categoriaPadreNombre;
    private int activo;
    private LocalDateTime fechaCreacion;
}
