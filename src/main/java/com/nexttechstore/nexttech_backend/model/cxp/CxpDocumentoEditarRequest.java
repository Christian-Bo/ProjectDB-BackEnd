package com.nexttechstore.nexttech_backend.model.cxp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload para sp_CXP_Documentos_Editar:
 *   @UsuarioId, @Id, @NumeroDocumento, @FechaEmision, @FechaVencimiento?, @Moneda, @MontoTotal
 * (proveedor_id, origen_* no se editan en el SP)
 */
@Data
public class CxpDocumentoEditarRequest {

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
