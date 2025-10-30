package com.nexttechstore.nexttech_backend.dto.cxc;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CxcPagoRequestDto {
    private Integer clienteId;
    private BigDecimal monto;
    private String formaPago;     // EFE/TAR/TRF/...
    private LocalDate fechaPago;  // opcional (el SP no lo usa)
    private String observaciones;

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
