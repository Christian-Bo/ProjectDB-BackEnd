package com.nexttechstore.nexttech_backend.dto;

import java.time.LocalDate;
import java.math.BigDecimal;

public class VentaDetalleDto {
    private Integer id;
    private Integer productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoLinea;
    private BigDecimal subtotal;
    private String lote;
    private LocalDate fechaVencimiento;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getDescuentoLinea() { return descuentoLinea; }
    public void setDescuentoLinea(BigDecimal descuentoLinea) { this.descuentoLinea = descuentoLinea; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
