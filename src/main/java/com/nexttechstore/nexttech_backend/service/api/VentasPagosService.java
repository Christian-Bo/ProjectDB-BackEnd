package com.nexttechstore.nexttech_backend.service.api;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface VentasPagosService {
    List<Map<String,Object>> listarPagos(Integer ventaId, Integer clienteId, Date desde, Date hasta);
    int crearPago(int ventaId, String formaPago, java.math.BigDecimal monto, String referencia);
    void eliminarPago(int pagoId);
    Map<String,Object> obtenerPago(int pagoId);
    void actualizarPago(int pagoId, String formaPago, java.math.BigDecimal monto, String referencia);
}
