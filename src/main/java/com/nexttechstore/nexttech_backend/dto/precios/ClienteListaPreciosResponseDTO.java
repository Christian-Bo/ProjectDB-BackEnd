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
public class ClienteListaPreciosResponseDTO {
    private Integer clienteId;
    private String clienteCodigo;
    private String clienteNombre;
    private Integer listaId;
    private String listaNombre;
    private String moneda;
    private Boolean activa;
    private LocalDateTime fechaAsignacion;
}