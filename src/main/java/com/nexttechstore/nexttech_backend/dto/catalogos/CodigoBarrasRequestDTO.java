package com.nexttechstore.nexttech_backend.dto.catalogos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodigoBarrasRequestDTO {

    @NotNull(message = "El ID del producto es requerido")
    private Integer productoId;

    @NotBlank(message = "El código de barras es requerido")
    @Size(max = 50, message = "El código de barras no puede exceder 50 caracteres")
    private String codigoBarras;

    // Acepta 'E', 'U', 'C' (mayús/minús). Valor por defecto 'E' si el builder no lo setea.
    @Default
    @Pattern(regexp = "(?i)[euc]", message = "El tipo de código debe ser E (EAN), U (UPC) o C (Custom)")
    private String tipoCodigo = "E";

    // Activo por defecto si no se setea mediante builder
    @Default
    private Boolean activo = true;
}
