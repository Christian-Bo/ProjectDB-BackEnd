package com.nexttechstore.nexttech_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CotizacionListItemDto {
    private Integer id;
    private String numeroCotizacion;
    private LocalDate fechaCotizacion;
    private BigDecimal total;
    private String estado;
    private Integer clienteId;
    private String clienteNombre;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNumeroCotizacion() { return numeroCotizacion; }
    public void setNumeroCotizacion(String numeroCotizacion) { this.numeroCotizacion = numeroCotizacion; }
    public LocalDate getFechaCotizacion() { return fechaCotizacion; }
    public void setFechaCotizacion(LocalDate fechaCotizacion) { this.fechaCotizacion = fechaCotizacion; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
}
