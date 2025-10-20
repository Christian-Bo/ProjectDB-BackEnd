package com.nexttechstore.nexttech_backend.dto.catalogos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CodigoBarrasDto {
    private int id;
    private int productoId;
    private String codigoBarras;
    private String tipoCodigo;
    private boolean activo;
    private LocalDateTime fechaCreacion;
}
