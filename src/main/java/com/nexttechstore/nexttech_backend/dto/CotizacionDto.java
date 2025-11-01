package com.nexttechstore.nexttech_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CotizacionDto {
    private Integer id;
    private String  numeroCotizacion;
    private LocalDate fechaCotizacion;
    private LocalDate fechaVigencia;
    private BigDecimal subtotal;
    private BigDecimal descuentoGeneral;
    private BigDecimal iva;
    private BigDecimal total;
    private String estado;                 // 'V' vigente, 'C' convertida, etc.
    private String observaciones;
    private String terminosCondiciones;

    private Integer clienteId;
    private String  clienteCodigo;
    private String  clienteNombre;

    private Integer vendedorId;
    private String  vendedorNombre;

    private List<CotizacionDetalleDto> items;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNumeroCotizacion() { return numeroCotizacion; }
    public void setNumeroCotizacion(String numeroCotizacion) { this.numeroCotizacion = numeroCotizacion; }
    public LocalDate getFechaCotizacion() { return fechaCotizacion; }
    public void setFechaCotizacion(LocalDate fechaCotizacion) { this.fechaCotizacion = fechaCotizacion; }
    public LocalDate getFechaVigencia() { return fechaVigencia; }
    public void setFechaVigencia(LocalDate fechaVigencia) { this.fechaVigencia = fechaVigencia; }
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
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getTerminosCondiciones() { return terminosCondiciones; }
    public void setTerminosCondiciones(String terminosCondiciones) { this.terminosCondiciones = terminosCondiciones; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getClienteCodigo() { return clienteCodigo; }
    public void setClienteCodigo(String clienteCodigo) { this.clienteCodigo = clienteCodigo; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public Integer getVendedorId() { return vendedorId; }
    public void setVendedorId(Integer vendedorId) { this.vendedorId = vendedorId; }
    public String getVendedorNombre() { return vendedorNombre; }
    public void setVendedorNombre(String vendedorNombre) { this.vendedorNombre = vendedorNombre; }
    public List<CotizacionDetalleDto> getItems() { return items; }
    public void setItems(List<CotizacionDetalleDto> items) { this.items = items; }
}
