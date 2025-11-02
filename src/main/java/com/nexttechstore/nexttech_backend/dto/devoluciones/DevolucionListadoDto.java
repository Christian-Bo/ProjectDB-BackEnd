package com.nexttechstore.nexttech_backend.dto.devoluciones;

public record DevolucionListadoDto(
        Integer id,
        String  numero,
        String  fecha,
        String  ventaNumero,
        String  clienteNombre,
        String  estado
) {}
