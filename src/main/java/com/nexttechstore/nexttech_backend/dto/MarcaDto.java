package com.nexttechstore.nexttech_backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarcaDto {
    private Integer id;

    @NotBlank
    private String nombre;

    @NotNull
    private Integer estado;
}
