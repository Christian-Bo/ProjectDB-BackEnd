package com.nexttechstore.nexttech_backend.model.cxp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad para dbo.cxp_documentos (snake_case).
 * Campos exactos del DDL y SPs.
 */
@Data
public class CxpDocumento {
    private Integer id;

    @NotNull(message = "proveedor_id es obligatorio.")
    private Integer proveedor_id;

    @NotBlank(message = "origen_tipo es obligatorio (C/F).")
    private String origen_tipo; // CHAR(1)

    @NotNull(message = "origen_id es obligatorio.")
    private Integer origen_id;

    @NotBlank(message = "numero_documento es obligatorio.")
    private String numero_documento;

    @NotNull(message = "fecha_emision es obligatoria.")
    private LocalDate fecha_emision;

    // Puede ser null
    private LocalDate fecha_vencimiento;

    @NotBlank(message = "moneda es obligatoria.")
    private String moneda; // NVARCHAR(10), default 'GTQ'

    @NotNull(message = "monto_total es obligatorio.")
    private BigDecimal monto_total;

    @NotNull(message = "saldo_pendiente es obligatorio.")
    private BigDecimal saldo_pendiente;

    @NotBlank(message = "estado es obligatorio.")
    private String estado; // 'P','C','A'

    // audit
    private java.time.OffsetDateTime fecha_creacion; // si mapeas DATETIME a OffsetDateTime, opcional
}
