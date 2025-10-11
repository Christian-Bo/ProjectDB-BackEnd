package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.model.cxp.*;
import com.nexttechstore.nexttech_backend.repository.sp.CxpAplicacionesSpRepository;
import com.nexttechstore.nexttech_backend.repository.sp.CxpDocumentosSpRepository;
import com.nexttechstore.nexttech_backend.repository.sp.CxpPagosSpRepository;
import com.nexttechstore.nexttech_backend.service.api.CxpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CxpServiceSpImpl implements CxpService {

    private final CxpDocumentosSpRepository docRepo;
    private final CxpPagosSpRepository pagosRepo;
    private final CxpAplicacionesSpRepository aplRepo;

    public CxpServiceSpImpl(CxpDocumentosSpRepository docRepo,
                            CxpPagosSpRepository pagosRepo,
                            CxpAplicacionesSpRepository aplRepo) {
        this.docRepo = docRepo;
        this.pagosRepo = pagosRepo;
        this.aplRepo = aplRepo;
    }

    // ===== Documentos =====
    @Override @Transactional(readOnly = true)
    public List<CxpDocumento> listarDocumentos(Integer proveedorId, String texto) {
        return docRepo.listar(proveedorId, texto);
    }

    @Override @Transactional
    public CxpDocumento crearDocumento(Integer usuarioId, CxpDocumentoRequest r) {
        return docRepo.crear(usuarioId, r);
    }

    @Override @Transactional
    public CxpDocumento editarDocumento(Integer usuarioId, Integer id, CxpDocumentoEditarRequest r) {
        return docRepo.editar(usuarioId, id, r);
    }

    @Override @Transactional
    public Integer anularDocumento(Integer usuarioId, Integer id) {
        return docRepo.anular(usuarioId, id);
    }

    // ===== Pagos =====
    @Override @Transactional(readOnly = true)
    public List<CxpPago> listarPagos(Integer proveedorId, String texto) {
        return pagosRepo.listar(proveedorId, texto);
    }

    @Override @Transactional
    public CxpPago crearPago(Integer usuarioId, CxpPagoRequest r) {
        return pagosRepo.crear(usuarioId, r);
    }

    @Override @Transactional
    public CxpPago editarPago(Integer usuarioId, Integer id, CxpPagoEditarRequest r) {
        return pagosRepo.editar(usuarioId, id, r);
    }

    @Override @Transactional
    public Integer eliminarPago(Integer usuarioId, Integer id) {
        return pagosRepo.eliminar(usuarioId, id);
    }

    @Override @Transactional
    public Integer anularPago(Integer usuarioId, Integer id) {
        return pagosRepo.anular(usuarioId, id);
    }

    // ===== Aplicaciones =====
    @Override @Transactional(readOnly = true)
    public List<CxpAplicacion> listarAplicaciones(Integer pagoId) {
        return aplRepo.listar(pagoId);
    }

    @Override @Transactional
    public List<CxpAplicacion> crearAplicacionesLote(Integer usuarioId, Integer pagoId, List<CxpAplicacionItemRequest> items) {
        return aplRepo.crearLote(usuarioId, pagoId, items);
    }
}
