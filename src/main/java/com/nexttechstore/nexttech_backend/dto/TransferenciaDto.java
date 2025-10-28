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
public class TransferenciaDto {
    private Integer id;
    private String numeroTransferencia;
    private LocalDate fechaTransferencia;
    private Integer bodegaOrigenId;
    private String bodegaOrigenNombre;
    private Integer bodegaDestinoId;
    private String bodegaDestinoNombre;
    private String estado;
    private String estadoDescripcion;
    private Integer solicitanteId;
    private Integer aprobadorId;
    private Integer receptorId;
    private LocalDateTime fechaAprobacion;
    private LocalDateTime fechaRecepcion;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private List<DetalleTransferenciaDto> detalles;
}