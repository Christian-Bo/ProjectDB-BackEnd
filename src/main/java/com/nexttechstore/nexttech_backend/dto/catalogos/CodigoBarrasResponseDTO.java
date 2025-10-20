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
public class CodigoBarrasResponseDTO {
    private Integer id;
    private Integer productoId;
    private String productoCodigo;
    private String productoNombre;
    private String codigoBarras;
    private String tipoCodigo;
    private String tipoCodigoNombre;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}