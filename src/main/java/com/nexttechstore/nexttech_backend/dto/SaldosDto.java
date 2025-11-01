package com.nexttechstore.nexttech_backend.dto;

import java.math.BigDecimal;

public class SaldosDto {
    private String origen;           // "CONTADO" / "CREDITO"
    private BigDecimal total;
    private BigDecimal pagado;
    private BigDecimal saldo;
    private Integer documentoId;     // nullable
    private Integer clienteId;       // nullable

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public BigDecimal getPagado() { return pagado; }
    public void setPagado(BigDecimal pagado) { this.pagado = pagado; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public Integer getDocumentoId() { return documentoId; }
    public void setDocumentoId(Integer documentoId) { this.documentoId = documentoId; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
}
