package com.nexttechstore.nexttech_backend.service.api;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface CxcPagosService {
    List<Map<String,Object>> listarPagos(Integer clienteId, Date desde, Date hasta);
    List<Map<String,Object>> listarAplicacionesPorPago(Integer pagoId);
}
