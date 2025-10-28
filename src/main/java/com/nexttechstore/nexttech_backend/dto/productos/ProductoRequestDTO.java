package com.nexttechstore.nexttech_backend.dto.productos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDTO {
    
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String codigo; // Puede ser null para generación automática
    
    @NotBlank(message = "El nombre del producto es requerido")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;
    
    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;
    
    @NotNull(message = "El precio de compra es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de compra debe ser mayor a 0")
    private BigDecimal precioCompra;
    
    private BigDecimal precioVenta; // Puede ser null para cálculo automático
    
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo = 0;
    
    @Min(value = 0, message = "El stock máximo no puede ser negativo")
    private Integer stockMaximo = 0;
    
    @Pattern(regexp = "[AID]", message = "El estado debe ser A (Activo), I (Inactivo) o D (Descontinuado)")
    private String estado = "A";
    
    private Integer marcaId;
    
    private Integer categoriaId;
    
    @Size(max = 50, message = "El código de barras no puede exceder 50 caracteres")
    private String codigoBarras;
    
    @NotBlank(message = "La unidad de medida es requerida")
    @Size(max = 20, message = "La unidad de medida no puede exceder 20 caracteres")
    private String unidadMedida = "UNIDAD";
    
    @DecimalMin(value = "0.0", message = "El peso no puede ser negativo")
    private BigDecimal peso;
    
    @Min(value = 0, message = "Los meses de garantía no pueden ser negativos")
    private Integer garantiaMeses = 0;
    
    @NotNull(message = "El empleado creador es requerido")
    private Integer creadoPor;
    
    private Boolean recalcularPrecio = false; // Para updates
}
