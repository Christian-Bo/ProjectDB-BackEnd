package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.model.compras.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Contrato del servicio de Compras.
 * No implementa negocio: solo orquesta llamadas al repositorio de SPs.
 */
public interface ComprasService {

    // ===== Compras (SPs) =====
    List<CompraListItem> listar(Date fechaDel, Date fechaAl, Integer proveedorId, String estado, String texto);

    CompraFull obtenerPorId(int compraId);

    int crear(CompraCrearRequest req);

    int editarCabecera(CompraEditarCabeceraRequest req);

    int agregarDetalle(int usuarioId, int compraId, List<CompraDetalleRequest> lineas);

    int editarDetalle(CompraEditarDetalleRequest req);

    int quitarDetalle(int usuarioId, int detalleId);

    int anular(CompraAnularRequest req);

    // ===== Catálogos para combos (sin crear DTOs nuevos) =====
    List<Map<String,Object>> catalogoProveedores(Boolean soloActivos);

    List<Map<String,Object>> catalogoBodegas();

    List<Map<String,Object>> catalogoEmpleados();

    List<Map<String,Object>> catalogoProductos(String texto, Integer limit);
}
