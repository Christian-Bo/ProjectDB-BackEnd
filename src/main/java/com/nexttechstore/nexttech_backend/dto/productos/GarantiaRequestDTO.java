package com.nexttechstore.nexttech_backend.dto.productos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarantiaRequestDTO {

    @NotNull(message = "El ID de la venta es requerido")
    private Integer ventaId;

    @NotNull(message = "El ID del detalle de venta es requerido")
    private Integer detalleVentaId;

    @Size(max = 100, message = "El número de serie no puede exceder 100 caracteres")
    private String numeroSerie;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de vencimiento es requerida")
    private LocalDate fechaVencimiento;

    @Pattern(regexp = "[VUE]", message = "El estado debe ser V (Vigente), U (Usada) o E (Expirada)")
    private String estado = "V";

    @Size(max = 2000, message = "Las observaciones no pueden exceder 2000 caracteres")
    private String observaciones;
}