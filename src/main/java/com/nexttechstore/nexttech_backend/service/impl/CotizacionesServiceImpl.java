package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.*;
import com.nexttechstore.nexttech_backend.repository.sp.CotizacionesSpRepository;
import com.nexttechstore.nexttech_backend.service.api.CotizacionesService;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CotizacionesServiceImpl implements CotizacionesService {

    private final CotizacionesSpRepository sp;

    public CotizacionesServiceImpl(CotizacionesSpRepository sp) {
        this.sp = sp;
    }

    @Override
    public List<CotizacionListItemDto> listar(LocalDate desde, LocalDate hasta, Integer clienteId, String numero, Integer page, Integer size) {
        try {
            var rows = sp.listar(desde, hasta, clienteId, numero, page, size);
            var out = new ArrayList<CotizacionListItemDto>();
            for (var r : rows) {
                var it = new CotizacionListItemDto();
                it.setId((Integer) r.get("id"));
                it.setNumeroCotizacion((String) r.get("numero_cotizacion"));
                if (r.get("fecha_cotizacion") instanceof LocalDate ld) it.setFechaCotizacion(ld);
                it.setTotal((java.math.BigDecimal) r.get("total"));
                it.setEstado((String) r.get("estado"));
                it.setClienteId((Integer) r.get("cliente_id"));
                it.setClienteNombre((String) r.get("cliente_nombre"));
                out.add(it);
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public CotizacionDto obtenerPorId(int id) {
        try {
            Map<String,Object> data = sp.getById(id);
            @SuppressWarnings("unchecked")
            Map<String,Object> h = (Map<String, Object>) data.get("header");
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> d = (List<Map<String, Object>>) data.get("detalle");

            var dto = new CotizacionDto();
            dto.setId((Integer) h.get("id"));
            dto.setNumeroCotizacion((String) h.get("numero_cotizacion"));

            var fv = h.get("fecha_cotizacion");
            if (fv instanceof java.sql.Date) dto.setFechaCotizacion(((java.sql.Date) fv).toLocalDate());
            else if (fv instanceof java.time.LocalDate) dto.setFechaCotizacion((LocalDate) fv);

            var vig = h.get("fecha_vigencia");
            if (vig instanceof java.sql.Date) dto.setFechaVigencia(((java.sql.Date) vig).toLocalDate());
            else if (vig instanceof java.time.LocalDate) dto.setFechaVigencia((LocalDate) vig);

            dto.setSubtotal((java.math.BigDecimal) h.get("subtotal"));
            dto.setDescuentoGeneral((java.math.BigDecimal) h.get("descuento_general"));
            dto.setIva((java.math.BigDecimal) h.get("iva"));
            dto.setTotal((java.math.BigDecimal) h.get("total"));
            dto.setEstado((String) h.get("estado"));
            dto.setObservaciones((String) h.get("observaciones"));
            dto.setTerminosCondiciones((String) h.get("terminos_condiciones"));

            dto.setClienteId((Integer) h.get("cliente_id"));
            dto.setClienteCodigo((String) h.get("cliente_codigo"));
            dto.setClienteNombre((String) h.get("cliente_nombre"));

            dto.setVendedorId((Integer) h.get("vendedor_id"));
            dto.setVendedorNombre((String) h.get("vendedor_nombre"));

            var items = new ArrayList<CotizacionDetalleDto>();
            for (var r : d) {
                var it = new CotizacionDetalleDto();
                it.setId((Integer) r.get("detalle_id"));
                it.setProductoId((Integer) r.get("producto_id"));
                it.setProductoCodigo((String) r.get("producto_codigo"));
                it.setProductoNombre((String) r.get("producto_nombre"));
                it.setCantidad((Integer) r.get("cantidad"));
                it.setPrecioUnitario((java.math.BigDecimal) r.get("precio_unitario"));
                it.setDescuentoLinea((java.math.BigDecimal) r.get("descuento_linea"));
                it.setSubtotal((java.math.BigDecimal) r.get("subtotal"));
                it.setDescripcionAdicional((String) r.get("descripcion_adicional"));
                items.add(it);
            }
            dto.setItems(items);
            return dto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public int crear(CotizacionCreateRequestDto req) {
        try {
            java.sql.Date vig = (req.getFechaVigencia() != null) ? Date.valueOf(req.getFechaVigencia()) : null;
            return sp.crear(
                    req.getClienteId(),
                    req.getVendedorId(),
                    vig,
                    req.getObservaciones(),
                    req.getTerminos(),
                    req.getDescuentoGeneral(),
                    req.getIva(),
                    req.getItems()
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public int convertirAVenta(int cotizacionId, int bodegaId, int serieId, Integer cajeroId) {
        try {
            return sp.convertirAVenta(cotizacionId, bodegaId, serieId, cajeroId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
