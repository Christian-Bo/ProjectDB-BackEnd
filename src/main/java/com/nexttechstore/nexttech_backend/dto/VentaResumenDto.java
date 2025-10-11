package com.nexttechstore.nexttech_backend.dto;

import java.time.LocalDate;
import java.math.BigDecimal;

public class VentaResumenDto {
    private Integer id;
    private String numeroVenta;
    private LocalDate fechaVenta;
    private Integer clienteId;
    private String clienteNombre;
    private BigDecimal total;
    private String estado;
    private String tipoPago;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNumeroVenta() { return numeroVenta; }
    public void setNumeroVenta(String numeroVenta) { this.numeroVenta = numeroVenta; }
    public LocalDate getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDate fechaVenta) { this.fechaVenta = fechaVenta; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }
}
