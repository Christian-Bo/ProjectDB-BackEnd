package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaPreciosCopiarRequestDTO {

    @NotNull(message = "El ID de la lista origen es requerido")
    private Integer listaOrigenId;

    @NotBlank(message = "El nombre de la nueva lista es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombreNuevaLista;

    private BigDecimal porcentajeAjuste = BigDecimal.ZERO;

    @Size(max = 10, message = "La moneda no puede exceder 10 caracteres")
    private String moneda = "GTQ";

    private Boolean activa = true;
}