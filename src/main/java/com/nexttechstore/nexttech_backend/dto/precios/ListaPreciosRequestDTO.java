package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaPreciosRequestDTO {

    @NotBlank(message = "El nombre de la lista es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "La moneda es requerida")
    @Size(max = 10, message = "La moneda no puede exceder 10 caracteres")
    private String moneda = "GTQ";

    private Boolean activa = true;
}