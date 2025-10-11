package com.nexttechstore.nexttech_backend.model.compras;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Payload para crear una compra completa: cabecera + detalle (TVP).
 * SP esperado: dbo.sp_COMPRAS_Crear
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraCrearRequest {

    @NotNull
    private Integer usuarioId;

    @NotBlank
    @Size(max = 20)
    private String numeroCompra;

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

    private String observaciones;

    @NotNull
    @Size(min = 1, message = "Debe enviar al menos una línea de detalle.")
    private List<CompraDetalleRequest> detalle;
}
