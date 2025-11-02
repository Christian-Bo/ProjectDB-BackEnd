package com.nexttechstore.nexttech_backend.model.cxp;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad para dbo.cxp_aplicaciones (snake_case)
 */
@Data
public class CxpAplicacion {
    private Integer id;

    @NotNull(message = "pago_id es obligatorio.")
    private Integer pago_id;

    // NUEVO: Información del pago (viene del SP con JOIN)
    private LocalDate pago_fecha;
    private String pago_forma_pago;

    @NotNull(message = "documento_id es obligatorio.")
    private Integer documento_id;

    // NUEVO: Información del documento (viene del SP con JOIN)
    private String documento_numero;
    private LocalDate documento_fecha_emision;
    private BigDecimal documento_monto_total;
    private BigDecimal documento_saldo_pendiente;

    @NotNull(message = "monto_aplicado es obligatorio.")
    private java.math.BigDecimal monto_aplicado;

    private LocalDate fecha_aplicacion;
}