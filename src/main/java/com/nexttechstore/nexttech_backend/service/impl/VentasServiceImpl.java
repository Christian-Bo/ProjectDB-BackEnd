package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.*;
import com.nexttechstore.nexttech_backend.repository.sp.VentasSpRepository;
import com.nexttechstore.nexttech_backend.service.api.VentasService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VentasServiceImpl implements VentasService {

    private final VentasSpRepository spRepo;

    public VentasServiceImpl(VentasSpRepository spRepo) {
        this.spRepo = spRepo;
    }

    @Override
    @Transactional
    public int registrar(VentaRequestDto req) {
        try {
            return spRepo.crearVenta(req);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public void anular(int ventaId, String motivo) {
        try {
            spRepo.anularVenta(ventaId, motivo);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public void editarHeader(int ventaId, VentaHeaderEditDto dto) {
        try {
            spRepo.editarHeader(ventaId, dto);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public void editarDetalle(int ventaId, List<VentaDetalleEditItemDto> items) {
        try {
            spRepo.editarDetalle(ventaId, items);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public VentaDto obtenerVentaPorId(int id) {
        try {
            Map<String,Object> data = spRepo.getVentaById(id);
            @SuppressWarnings("unchecked")
            Map<String,Object> h = (Map<String,Object>) data.get("header");
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> d = (List<Map<String,Object>>) data.get("detalle");

            VentaDto dto = new VentaDto();
            dto.setId((Integer) h.get("id"));
            dto.setNumeroVenta((String) h.get("numero_venta"));

            Object fv = h.get("fecha_venta");
            if (fv instanceof java.sql.Date) {
                dto.setFechaVenta(((java.sql.Date) fv).toLocalDate());
            } else if (fv instanceof java.sql.Timestamp) {
                dto.setFechaVenta(((java.sql.Timestamp) fv).toLocalDateTime().toLocalDate());
            }

            dto.setSubtotal((java.math.BigDecimal) h.get("subtotal"));
            dto.setDescuentoGeneral((java.math.BigDecimal) h.get("descuento_general"));
            dto.setIva((java.math.BigDecimal) h.get("iva"));
            dto.setTotal((java.math.BigDecimal) h.get("total"));

            dto.setEstado((String) h.get("estado"));
            dto.setTipoPago((String) h.get("tipo_pago"));

            dto.setObservaciones((String) h.get("observaciones"));
            dto.setClienteId((Integer) h.get("cliente_id"));
            dto.setClienteNombre((String) h.getOrDefault("cliente_nombre", null));
            dto.setVendedorId((Integer) h.getOrDefault("vendedor_id", null));
            dto.setCajeroId((Integer) h.getOrDefault("cajero_id", null));
            dto.setBodegaOrigenId((Integer) h.getOrDefault("bodega_origen_id", null));

            List<VentaDetalleDto> items = new ArrayList<>();
            for (var r : d) {
                VentaDetalleDto item = new VentaDetalleDto();
                item.setId((Integer) r.get("detalle_id"));
                item.setProductoId((Integer) r.get("producto_id"));
                item.setCantidad((Integer) r.get("cantidad"));
                item.setPrecioUnitario((java.math.BigDecimal) r.get("precio_unitario"));
                item.setDescuentoLinea((java.math.BigDecimal) r.get("descuento_linea"));
                item.setSubtotal((java.math.BigDecimal) r.get("subtotal"));

                // NUEVO: lote y fecha de vencimiento si vienen
                item.setLote((String) r.getOrDefault("lote", null));
                Object fv2 = r.get("fecha_vencimiento");
                if (fv2 instanceof java.sql.Date) {
                    item.setFechaVencimiento(((java.sql.Date) fv2).toLocalDate());
                } else if (fv2 instanceof java.sql.Timestamp) {
                    item.setFechaVencimiento(((java.sql.Timestamp) fv2).toLocalDateTime().toLocalDate());
                }

                items.add(item);
            }
            dto.setItems(items);
            return dto;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<VentaResumenDto> listarVentas(LocalDate desde, LocalDate hasta,
                                              Integer clienteId, String numeroVenta,
                                              Boolean incluirAnuladas,
                                              Integer page, Integer size) {
        try {
            var rows = spRepo.listarVentas(desde, hasta, clienteId, numeroVenta, incluirAnuladas, page, size);
            var list = new ArrayList<VentaResumenDto>();
            for (var r : rows) {
                var v = new VentaResumenDto();
                v.setId((Integer) r.get("id"));
                v.setNumeroVenta((String) r.get("numero_venta"));

                Object fv = r.get("fecha_venta");
                if (fv instanceof java.sql.Date) {
                    v.setFechaVenta(((java.sql.Date) fv).toLocalDate());
                } else if (fv instanceof java.sql.Timestamp) {
                    v.setFechaVenta(((java.sql.Timestamp) fv).toLocalDateTime().toLocalDate());
                }

                v.setTotal((java.math.BigDecimal) r.get("total"));
                v.setClienteId((Integer) r.get("cliente_id"));
                v.setClienteNombre((String) r.get("cliente_nombre"));
                v.setEstado((String) r.get("estado"));
                v.setTipoPago((String) r.get("tipo_pago"));
                list.add(v);
            }
            return list;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
