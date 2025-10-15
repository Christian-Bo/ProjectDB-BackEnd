package com.nexttechstore.nexttech_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DevolucionCreateRequest {

    @NotNull @Min(1)
    private Integer ventaId;       // @p_venta_id

    @NotNull @Min(1)
    private Integer aprobadaPor;   // @p_aprobada_por

    @NotEmpty
    private List<DevolucionItemDto> items; // TVP (detalle_venta_id, producto_id, cantidad, observaciones)
}
