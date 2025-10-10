package com.nexttechstore.nexttech_backend.exception;

import java.math.BigDecimal;

public class StockInsuficienteException extends RuntimeException {
    private final int productoId;
    private final int bodegaId;
    private final BigDecimal disponible;
    private final BigDecimal solicitado;

    public StockInsuficienteException(int productoId, int bodegaId, BigDecimal disponible, BigDecimal solicitado) {
        super(String.format("Stock insuficiente. Producto=%d, Bodega=%d, disponible=%s, solicitado=%s",
                productoId, bodegaId, disponible, solicitado));
        this.productoId = productoId;
        this.bodegaId = bodegaId;
        this.disponible = disponible;
        this.solicitado = solicitado;
    }

    public int getProductoId() { return productoId; }
    public int getBodegaId() { return bodegaId; }
    public BigDecimal getDisponible() { return disponible; }
    public BigDecimal getSolicitado() { return solicitado; }
}
