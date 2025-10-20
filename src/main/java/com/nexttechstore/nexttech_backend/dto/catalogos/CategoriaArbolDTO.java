package com.nexttechstore.nexttech_backend.dto.catalogos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaArbolDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Integer categoriaPadreId;
    private String ruta;
    private Integer nivel;
    private String nombreIndentado;
}