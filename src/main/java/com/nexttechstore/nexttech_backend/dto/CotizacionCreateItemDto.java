package com.nexttechstore.nexttech_backend.dto;

import java.math.BigDecimal;

public class CotizacionCreateItemDto {
    private Integer productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoLinea;
    private String  descripcionAdicional;

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getDescuentoLinea() { return descuentoLinea; }
    public void setDescuentoLinea(BigDecimal descuentoLinea) { this.descuentoLinea = descuentoLinea; }
    public String getDescripcionAdicional() { return descripcionAdicional; }
    public void setDescripcionAdicional(String descripcionAdicional) { this.descripcionAdicional = descripcionAdicional; }
}
