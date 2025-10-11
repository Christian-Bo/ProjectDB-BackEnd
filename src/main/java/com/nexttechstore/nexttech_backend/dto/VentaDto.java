package com.nexttechstore.nexttech_backend.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.math.BigDecimal;

public class VentaDto {
    private Integer id;
    private String numeroVenta;
    private LocalDate fechaVenta;
    private BigDecimal subtotal;
    private BigDecimal descuentoGeneral;
    private BigDecimal iva;
    private BigDecimal total;
    private String estado;
    private String tipoPago;
    private String observaciones;
    private Integer clienteId;
    private String clienteNombre;
    private Integer vendedorId;
    private Integer cajeroId;
    private Integer bodegaOrigenId;
    private OffsetDateTime fechaCreacion;
    private List<VentaDetalleDto> items;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNumeroVenta() { return numeroVenta; }
    public void setNumeroVenta(String numeroVenta) { this.numeroVenta = numeroVenta; }
    public LocalDate getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDate fechaVenta) { this.fechaVenta = fechaVenta; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getDescuentoGeneral() { return descuentoGeneral; }
    public void setDescuentoGeneral(BigDecimal descuentoGeneral) { this.descuentoGeneral = descuentoGeneral; }
    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public Integer getVendedorId() { return vendedorId; }
    public void setVendedorId(Integer vendedorId) { this.vendedorId = vendedorId; }
    public Integer getCajeroId() { return cajeroId; }
    public void setCajeroId(Integer cajeroId) { this.cajeroId = cajeroId; }
    public Integer getBodegaOrigenId() { return bodegaOrigenId; }
    public void setBodegaOrigenId(Integer bodegaOrigenId) { this.bodegaOrigenId = bodegaOrigenId; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<VentaDetalleDto> getItems() { return items; }
    public void setItems(List<VentaDetalleDto> items) { this.items = items; }
}
