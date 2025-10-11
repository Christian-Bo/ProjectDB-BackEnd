package com.nexttechstore.nexttech_backend.model.cxp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload para sp_CXP_Documentos_Crear.
 *   @UsuarioId, @ProveedorId, @OrigenTipo, @OrigenId,
 *   @NumeroDocumento, @FechaEmision, @FechaVencimiento?,
 *   @Moneda?, @MontoTotal, @DocumentoIdOut OUTPUT
 */
@Data
public class CxpDocumentoRequest {

    @NotNull(message = "proveedor_id es obligatorio.")
    private Integer proveedor_id;

    @NotBlank(message = "origen_tipo es obligatorio (C/F).")
    private String origen_tipo;

    @NotNull(message = "origen_id es obligatorio.")
    private Integer origen_id;

    @NotBlank(message = "numero_documento es obligatorio.")
    private String numero_documento;

    @NotNull(message = "fecha_emision es obligatoria.")
    private LocalDate fecha_emision;

    private LocalDate fecha_vencimiento;

    @NotBlank(message = "moneda es obligatoria.")
    private String moneda;

    @NotNull(message = "monto_total es obligatorio.")
    private BigDecimal monto_total;
}
