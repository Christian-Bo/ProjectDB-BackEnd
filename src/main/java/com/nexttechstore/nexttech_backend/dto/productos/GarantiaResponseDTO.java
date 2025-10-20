package com.nexttechstore.nexttech_backend.dto.productos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarantiaResponseDTO {
    private Integer id;
    private Integer ventaId;
    private String numeroVenta;
    private Integer detalleVentaId;
    private String productoCodigo;
    private String productoNombre;
    private String numeroSerie;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private String estado;
    private String estadoNombre;
    private String observaciones;
    private String clienteCodigo;
    private String clienteNombre;
    private String clienteTelefono;
    private Integer diasRestantes;
}