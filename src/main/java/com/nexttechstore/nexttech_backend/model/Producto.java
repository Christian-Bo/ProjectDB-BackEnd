package com.nexttechstore.nexttech_backend.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    private Integer id;

    @NotBlank(message = "El SKU es obligatorio y no puede estar vacío.")
    @Size(max = 50, message = "El SKU no debe exceder 50 caracteres.")
    private String sku; // mapea a 'codigo' en la BD

    @NotBlank(message = "El nombre del producto es obligatorio y no puede estar vacío.")
    @Size(max = 200, message = "El nombre no debe exceder 200 caracteres.")
    private String nombre;

    @NotNull(message = "La categoría es obligatoria.")
    private Integer categoriaId;

    @NotNull(message = "La marca es obligatoria.")
    private Integer marcaId;

    @NotNull(message = "El estado es obligatorio (1=activo, 0=inactivo).")
    @Min(value = 0, message = "El estado debe ser 0 o 1.")
    @Max(value = 1, message = "El estado debe ser 0 o 1.")
    private Integer estado; // 1=activo, 0=inactivo (borrado lógico)

    // ---- Opcionales usados por los SPs (se envían como NULL si no los llenas) ----

    @Size(max = 500, message = "La descripción no debe exceder 500 caracteres.")
    private String descripcion;

    // costo ↔ precio_compra
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo debe ser mayor o igual a 0.")
    private Double costo;

    // precio_venta
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio de venta debe ser mayor o igual a 0.")
    private Double precioVenta;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo.")
    private Integer stockMinimo;

    @Min(value = 0, message = "El stock máximo no puede ser negativo.")
    private Integer stockMaximo;

    @Size(max = 100, message = "El código de barras no debe exceder 100 caracteres.")
    private String codigoBarras;

    @Size(max = 50, message = "La unidad de medida no debe exceder 50 caracteres.")
    private String unidadMedida;

    @DecimalMin(value = "0.0", inclusive = true, message = "El peso debe ser mayor o igual a 0.")
    private Double peso;

    @Min(value = 0, message = "La garantía (meses) no puede ser negativa.")
    private Integer garantiaMeses;
}

