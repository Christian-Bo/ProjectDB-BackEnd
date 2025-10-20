package com.nexttechstore.nexttech_backend.dto.catalogos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Integer categoriaPadreId;
    private String nombrePadre;
    private LocalDateTime fechaCreacion;
    private Integer totalProductos;
    private Integer totalSubcategorias;
}
