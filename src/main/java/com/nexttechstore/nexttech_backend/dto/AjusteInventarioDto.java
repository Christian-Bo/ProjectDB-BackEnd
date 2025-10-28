package com.nexttechstore.nexttech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AjusteInventarioDto {
    private Integer id;
    private String numeroAjuste;
    private LocalDate fechaAjuste;
    private Integer bodegaId;
    private String bodegaNombre;
    private String tipoAjuste;
    private String tipoAjusteDescripcion;
    private String motivo;
    private Integer responsableId;
    private String responsableNombre;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private List<DetalleAjusteDto> detalles;
}