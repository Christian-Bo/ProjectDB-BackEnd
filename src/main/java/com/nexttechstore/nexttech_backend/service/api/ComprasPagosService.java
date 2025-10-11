package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.model.compras.CompraPago;
import com.nexttechstore.nexttech_backend.model.compras.CompraPagoCrearRequest;
import com.nexttechstore.nexttech_backend.model.compras.CompraPagoEditarRequest;

import java.util.List;

public interface ComprasPagosService {
    List<CompraPago> listar(Integer compraId, String texto);
    CompraPago crear(Integer usuarioId, CompraPagoCrearRequest req);
    CompraPago editar(Integer usuarioId, Integer id, CompraPagoEditarRequest req);
    Integer eliminar(Integer usuarioId, Integer id);
}
