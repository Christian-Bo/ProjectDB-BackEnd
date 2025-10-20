package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteAsignarListaRequestDTO {

    @NotNull(message = "El ID del cliente es requerido")
    private Integer clienteId;

    @NotNull(message = "El ID de la lista es requerido")
    private Integer listaId;
}