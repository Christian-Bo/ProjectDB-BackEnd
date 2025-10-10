package com.nexttechstore.nexttech_backend.model.compras;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Payload para editar SOLO la cabecera de una compra.
 * SP esperado: dbo.sp_COMPRAS_EditarCabecera
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraEditarCabeceraRequest {

    @NotNull
    private Integer usuarioId;

    @NotNull
    private Integer compraId;

    @NotBlank
    @Size(max = 50)
    private String noFacturaProveedor;

    @NotNull
    private LocalDate fechaCompra;

    @NotNull
    private Integer proveedorId;

    @NotNull
    private Integer empleadoCompradorId;

    private Integer empleadoAutorizaId;

    @NotNull
    private Integer bodegaDestinoId;

    @DecimalMin(value = "0.0", inclusive = true)
    private Double descuentoGeneral;

    private String observaciones;
}
