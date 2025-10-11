package com.nexttechstore.nexttech_backend.model.compras;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Línea de detalle de la compra.
 * Si agregas columnas nuevas en tus SPs (ej. bonificación, impuestos línea),
 * agrégalas aquí y en el mapper del repositorio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraDetalle {

    private Integer id;
    private Integer compraId;

    // FK producto
    private Integer productoId;

    // Enriquecimiento informativo
    private String productoNombre;

    private Integer cantidadPedida;
    private Integer cantidadRecibida;

    private BigDecimal precioUnitario;
    private BigDecimal descuentoLinea;
    private BigDecimal subtotal;

    private String lote;
    private LocalDate fechaVencimiento;
}
