package com.nexttechstore.nexttech_backend.dto.precios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientePrecioProductoResponseDTO {
    private BigDecimal precio;
    private String origen; // "Lista de Precios", "Precio Especial", "Precio Base"
    private String mensaje;
}