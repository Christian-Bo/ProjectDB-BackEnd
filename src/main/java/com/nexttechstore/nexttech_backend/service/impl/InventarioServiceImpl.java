package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.AlertaInventarioDto;
import com.nexttechstore.nexttech_backend.dto.InventarioDto;
import com.nexttechstore.nexttech_backend.repository.sp.InventarioSpRepository;
import com.nexttechstore.nexttech_backend.service.api.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private InventarioSpRepository inventarioSpRepository;

    @Override
    public List<InventarioDto> listarInventario(Integer bodegaId) {
        List<Map<String, Object>> result = inventarioSpRepository.listarInventario(bodegaId);
        List<InventarioDto> inventarios = new ArrayList<>();

        for (Map<String, Object> row : result) {
            InventarioDto dto = mapearInventario(row);
            inventarios.add(dto);
        }

        return inventarios;
    }

    @Override
    public List<InventarioDto> inventarioPorProducto(Integer productoId) {
        List<Map<String, Object>> result = inventarioSpRepository.inventarioPorProducto(productoId);
        List<InventarioDto> inventarios = new ArrayList<>();

        for (Map<String, Object> row : result) {
            InventarioDto dto = new InventarioDto();
            dto.setId((Integer) row.get("id"));
            dto.setBodegaId((Integer) row.get("bodega_id"));
            dto.setBodegaNombre((String) row.get("bodega_nombre"));
            dto.setCantidadDisponible((Integer) row.get("cantidad_disponible"));
            dto.setCantidadReservada((Integer) row.get("cantidad_reservada"));
            dto.setCantidadTransito((Integer) row.get("cantidad_transito"));
            dto.setStockMinimo((Integer) row.get("stock_minimo"));
            dto.setStockMaximo((Integer) row.get("stock_maximo"));
            inventarios.add(dto);
        }

        return inventarios;
    }

    @Override
    public List<InventarioDto> inventarioPorBodega(Integer bodegaId) {
        List<Map<String, Object>> result = inventarioSpRepository.inventarioPorBodega(bodegaId);
        List<InventarioDto> inventarios = new ArrayList<>();

        for (Map<String, Object> row : result) {
            InventarioDto dto = new InventarioDto();
            dto.setProductoId((Integer) row.get("producto_id"));
            dto.setProductoCodigo((String) row.get("producto_codigo"));
            dto.setProductoNombre((String) row.get("producto_nombre"));
            dto.setCantidadDisponible((Integer) row.get("cantidad_disponible"));
            dto.setCantidadReservada((Integer) row.get("cantidad_reservada"));
            dto.setStockMinimo((Integer) row.get("stock_minimo"));
            dto.setStockMaximo((Integer) row.get("stock_maximo"));
            inventarios.add(dto);
        }

        return inventarios;
    }

    @Override
    public List<InventarioDto> listarKardex(Integer productoId, Integer bodegaId, String fechaDesde, String fechaHasta) {
        List<Map<String, Object>> result = inventarioSpRepository.listarKardex(productoId, bodegaId, fechaDesde, fechaHasta);
        List<InventarioDto> kardex = new ArrayList<>();

        for (Map<String, Object> row : result) {
            InventarioDto dto = new InventarioDto();
            dto.setId((Integer) row.get("id"));
            dto.setProductoId((Integer) row.get("producto_id"));
            dto.setProductoCodigo((String) row.get("producto_codigo"));
            dto.setProductoNombre((String) row.get("producto_nombre"));
            dto.setBodegaId((Integer) row.get("bodega_id"));
            dto.setBodegaNombre((String) row.get("bodega_nombre"));
            kardex.add(dto);
        }

        return kardex;
    }

    @Override
    public List<AlertaInventarioDto> listarAlertas(Integer bodegaId) {
        List<Map<String, Object>> result = inventarioSpRepository.listarAlertas(bodegaId);
        List<AlertaInventarioDto> alertas = new ArrayList<>();

        for (Map<String, Object> row : result) {
            AlertaInventarioDto dto = new AlertaInventarioDto();
            dto.setId((Integer) row.get("id"));
            dto.setProductoId((Integer) row.get("producto_id"));
            dto.setProductoCodigo((String) row.get("producto_codigo"));
            dto.setProductoNombre((String) row.get("producto_nombre"));
            dto.setBodegaId((Integer) row.get("bodega_id"));
            dto.setBodegaNombre((String) row.get("bodega_nombre"));
            dto.setStockMinimo((Integer) row.get("stock_minimo"));
            dto.setStockMaximo((Integer) row.get("stock_maximo"));
            dto.setStockActual((Integer) row.get("stock_actual"));
            dto.setTipoAlerta((String) row.get("tipo_alerta"));
            dto.setActivo((Boolean) row.get("activo"));
            alertas.add(dto);
        }

        return alertas;
    }

    private InventarioDto mapearInventario(Map<String, Object> row) {
        InventarioDto dto = new InventarioDto();
        dto.setId((Integer) row.get("id"));
        dto.setProductoId((Integer) row.get("producto_id"));
        dto.setProductoCodigo((String) row.get("producto_codigo"));
        dto.setProductoNombre((String) row.get("producto_nombre"));
        dto.setBodegaId((Integer) row.get("bodega_id"));
        dto.setBodegaNombre((String) row.get("bodega_nombre"));
        dto.setCantidadDisponible((Integer) row.get("cantidad_disponible"));
        dto.setCantidadReservada((Integer) row.get("cantidad_reservada"));
        dto.setCantidadTransito((Integer) row.get("cantidad_transito"));
        dto.setStockMinimo((Integer) row.get("stock_minimo"));
        dto.setStockMaximo((Integer) row.get("stock_maximo"));
        dto.setEstadoStock((String) row.get("estado_stock"));

        Object ultimaActualizacion = row.get("ultima_actualizacion");
        if (ultimaActualizacion != null) {
            dto.setUltimaActualizacion(LocalDateTime.parse(ultimaActualizacion.toString().replace(" ", "T")));
        }

        return dto;
    }
}