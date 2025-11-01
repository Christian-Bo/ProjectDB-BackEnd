package com.nexttechstore.nexttech_backend.dto;

import java.math.BigDecimal;

public class CotizacionDetalleDto {
    private Integer id;                 // detalle_id
    private Integer productoId;
    private String  productoCodigo;
    private String  productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoLinea;
    private BigDecimal subtotal;
    private String  descripcionAdicional;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public String getProductoCodigo() { return productoCodigo; }
    public void setProductoCodigo(String productoCodigo) { this.productoCodigo = productoCodigo; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getDescuentoLinea() { return descuentoLinea; }
    public void setDescuentoLinea(BigDecimal descuentoLinea) { this.descuentoLinea = descuentoLinea; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public String getDescripcionAdicional() { return descripcionAdicional; }
    public void setDescripcionAdicional(String descripcionAdicional) { this.descripcionAdicional = descripcionAdicional; }
}
