package com.nexttechstore.nexttech_backend.dto.cxc;

import java.math.BigDecimal;

public class CxcAplicacionItemDto {
    private Integer documentoId;
    private BigDecimal monto;

    public Integer getDocumentoId() { return documentoId; }
    public void setDocumentoId(Integer documentoId) { this.documentoId = documentoId; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
}
