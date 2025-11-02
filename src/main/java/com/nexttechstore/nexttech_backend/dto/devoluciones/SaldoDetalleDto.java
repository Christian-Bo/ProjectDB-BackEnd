package com.nexttechstore.nexttech_backend.dto.devoluciones;

public record SaldoDetalleDto(
        Integer detalleVentaId,
        Integer productoId,
        Integer vendido,
        Integer devuelto,
        Integer saldo
) {}
