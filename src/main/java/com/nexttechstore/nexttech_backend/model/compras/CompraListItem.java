package com.nexttechstore.nexttech_backend.model.compras;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Item liviano para listados (GET /api/compras).
 * Devuelve lo necesario para grillas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraListItem {
    private Integer id;
    private String numeroCompra;
    private String noFacturaProveedor;
    private LocalDate fechaCompra;

    private BigDecimal subtotal;
    private BigDecimal descuentoGeneral;
    private BigDecimal iva;
    private BigDecimal total;

    private String estado;

    // FKs + nombres
    private Integer proveedorId;
    private String proveedorNombre;

    private Integer bodegaDestinoId;
    private String bodegaDestinoNombre;

    private OffsetDateTime fechaCreacion;
}
