package com.nexttechstore.nexttech_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VentaDetalleEditItemDto {
    private Integer detalleId;          // null para 'A' (agregar)
    private Integer productoId;         // requerido
    private Integer bodegaId;           // requerido
    private Integer cantidad;           // requerido
    private BigDecimal precioUnitario;  // requerido
    private BigDecimal descuentoLinea;  // opcional
    private String accion;              // 'A', 'U', 'D'
}
