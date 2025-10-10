package com.nexttechstore.nexttech_backend.model.compras;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Cabecera de compra (enriquecida con nombres de FK).
 * NOTA: Los campos *_Nombre NO se envían a los SPs, solo son de lectura para la API.
 * Si en tus SPs cambian nombres de columnas devueltas, ajusta los mapeos en el repositorio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraCabecera {

    private Integer id;
    private String numeroCompra;
    private String noFacturaProveedor;
    private LocalDate fechaCompra;

    private BigDecimal subtotal;
    private BigDecimal descuentoGeneral;
    private BigDecimal iva;
    private BigDecimal total;

    /** Estados definidos por el negocio (ej.: P=Pendiente, R=Recibida, C=Cerrada, X=Anulada). */
    private String estado;

    private String observaciones;

    // FKs (IDs)
    private Integer proveedorId;
    private Integer empleadoCompradorId;
    private Integer empleadoAutorizaId;
    private Integer bodegaDestinoId;

    // Enriquecimiento (nombres legibles)
    private String proveedorNombre;
    private String empleadoCompradorNombre;
    private String empleadoAutorizaNombre;
    private String bodegaDestinoNombre;

    private OffsetDateTime fechaCreacion;
}
