package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.model.cxp.*;

import java.util.List;

public interface CxpService {
    // Documentos
    List<CxpDocumento> listarDocumentos(Integer proveedorId, String texto);
    CxpDocumento crearDocumento(Integer usuarioId, CxpDocumentoRequest r);
    CxpDocumento editarDocumento(Integer usuarioId, Integer id, CxpDocumentoEditarRequest r);
    Integer anularDocumento(Integer usuarioId, Integer id);

    // Pagos
    List<CxpPago> listarPagos(Integer proveedorId, String texto);
    CxpPago crearPago(Integer usuarioId, CxpPagoRequest r);
    CxpPago editarPago(Integer usuarioId, Integer id, CxpPagoEditarRequest r);
    Integer eliminarPago(Integer usuarioId, Integer id);
    Integer anularPago(Integer usuarioId, Integer id);

    // Aplicaciones
    List<CxpAplicacion> listarAplicaciones(Integer pagoId);
    List<CxpAplicacion> crearAplicacionesLote(Integer usuarioId, Integer pagoId, List<CxpAplicacionItemRequest> items);
}
