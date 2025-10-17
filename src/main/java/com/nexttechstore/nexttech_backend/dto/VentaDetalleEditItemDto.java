package com.nexttechstore.nexttech_backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VentaDetalleEditItemDto {
    private Integer detalleId;          // null para 'A' (agregar)
    private Integer productoId;         // requerido
    private Integer bodegaId;           // requerido para validar/afectar inventario
    private Integer cantidad;           // requerido (INT)
    private BigDecimal precioUnitario;  // requerido
    private BigDecimal descuentoLinea;  // opcional
    private String accion;              // 'A', 'U', 'D'
    private String lote;                // NUEVO
    private LocalDate fechaVencimiento; // NUEVO
}
