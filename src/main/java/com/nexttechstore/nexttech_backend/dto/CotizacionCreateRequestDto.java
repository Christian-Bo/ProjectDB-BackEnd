package com.nexttechstore.nexttech_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CotizacionCreateRequestDto {
    private Integer clienteId;
    private Integer vendedorId;
    private LocalDate fechaVigencia;
    private String  observaciones;
    private String  terminos;
    private BigDecimal descuentoGeneral;  // monto
    private BigDecimal iva;               // monto
    private List<CotizacionCreateItemDto> items;

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public Integer getVendedorId() { return vendedorId; }
    public void setVendedorId(Integer vendedorId) { this.vendedorId = vendedorId; }
    public LocalDate getFechaVigencia() { return fechaVigencia; }
    public void setFechaVigencia(LocalDate fechaVigencia) { this.fechaVigencia = fechaVigencia; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getTerminos() { return terminos; }
    public void setTerminos(String terminos) { this.terminos = terminos; }
    public BigDecimal getDescuentoGeneral() { return descuentoGeneral; }
    public void setDescuentoGeneral(BigDecimal descuentoGeneral) { this.descuentoGeneral = descuentoGeneral; }
    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }
    public List<CotizacionCreateItemDto> getItems() { return items; }
    public void setItems(List<CotizacionCreateItemDto> items) { this.items = items; }
}
