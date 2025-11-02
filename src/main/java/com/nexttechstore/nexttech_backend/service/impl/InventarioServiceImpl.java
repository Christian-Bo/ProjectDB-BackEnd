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
        if (result == null) return inventarios;

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
        if (result == null) return inventarios;

        for (Map<String, Object> row : result) {
            InventarioDto dto = new InventarioDto();
            dto.setId(asInt(row.get("id")));
            dto.setBodegaId(asInt(row.get("bodega_id")));
            dto.setBodegaNombre(asStr(row.get("bodega_nombre")));
            dto.setCantidadDisponible(asInt(row.get("cantidad_disponible")));
            dto.setCantidadReservada(asInt(row.get("cantidad_reservada")));
            dto.setCantidadTransito(asInt(row.get("cantidad_transito")));
            dto.setStockMinimo(asInt(row.get("stock_minimo")));
            dto.setStockMaximo(asInt(row.get("stock_maximo")));
            inventarios.add(dto);
        }

        return inventarios;
    }

    @Override
    public List<InventarioDto> inventarioPorBodega(Integer bodegaId) {
        List<Map<String, Object>> result = inventarioSpRepository.inventarioPorBodega(bodegaId);
        List<InventarioDto> inventarios = new ArrayList<>();
        if (result == null) return inventarios;

        for (Map<String, Object> row : result) {
            InventarioDto dto = new InventarioDto();
            dto.setProductoId(asInt(row.get("producto_id")));

            // Alias seguros sin tocar tu DTO
            String cod = asStr(row.get("producto_codigo"));
            if (cod == null) cod = asStr(row.get("codigo_producto"));
            dto.setProductoCodigo(cod);

            String nom = asStr(row.get("producto_nombre"));
            if (nom == null) nom = asStr(row.get("nombre_producto"));
            dto.setProductoNombre(nom);

            dto.setCantidadDisponible(asInt(row.get("cantidad_disponible")));
            dto.setCantidadReservada(asInt(row.get("cantidad_reservada")));
            dto.setStockMinimo(asInt(row.get("stock_minimo")));
            dto.setStockMaximo(asInt(row.get("stock_maximo")));
            inventarios.add(dto);
        }

        return inventarios;
    }

    @Override
    public List<InventarioDto> listarKardex(Integer productoId, Integer bodegaId, String fechaDesde, String fechaHasta) {
        // IMPORTANTE: ahora llamamos al SP real de movimientos:
        // InventarioSpRepository.listarMovimientos(fechaDesde, fechaHasta, productoId, bodegaId, tipo)
        List<Map<String, Object>> result = inventarioSpRepository.listarMovimientos(fechaDesde, fechaHasta, productoId, bodegaId, null);
        List<InventarioDto> kardex = new ArrayList<>();
        if (result == null) return kardex;

        for (Map<String, Object> row : result) {
            InventarioDto dto = new InventarioDto();

            // Campos básicos del producto y bodega (tu DTO no incluye detalles de movimiento)
            dto.setId(asInt(row.get("id")));
            dto.setProductoId(asInt(row.get("producto_id")));

            String cod = asStr(row.get("producto_codigo"));
            if (cod == null) cod = asStr(row.get("codigo_producto"));
            dto.setProductoCodigo(cod);

            String nom = asStr(row.get("producto_nombre"));
            if (nom == null) nom = asStr(row.get("nombre_producto"));
            dto.setProductoNombre(nom);

            dto.setBodegaId(asInt(row.get("bodega_id")));
            dto.setBodegaNombre(asStr(row.get("bodega_nombre")));

            // Nos mantenemos en tu estructura sin agregar setters extra aquí
            kardex.add(dto);
        }

        return kardex;
    }

    @Override
    public List<AlertaInventarioDto> listarAlertas(Integer bodegaId) {
        List<Map<String, Object>> result = inventarioSpRepository.listarAlertas(bodegaId);
        List<AlertaInventarioDto> alertas = new ArrayList<>();
        if (result == null) return alertas;

        for (Map<String, Object> row : result) {
            AlertaInventarioDto dto = new AlertaInventarioDto();
            dto.setId(asInt(row.get("id")));
            dto.setProductoId(asInt(row.get("producto_id")));
            dto.setProductoCodigo(asStr(row.get("producto_codigo")));
            dto.setProductoNombre(asStr(row.get("producto_nombre")));
            dto.setBodegaId(asInt(row.get("bodega_id")));
            dto.setBodegaNombre(asStr(row.get("bodega_nombre")));
            dto.setStockMinimo(asInt(row.get("stock_minimo")));
            dto.setStockMaximo(asInt(row.get("stock_maximo")));
            dto.setStockActual(asInt(row.get("stock_actual")));
            dto.setTipoAlerta(asStr(row.get("tipo_alerta")));
            Object activo = row.get("activo");
            dto.setActivo(activo instanceof Boolean ? (Boolean) activo : "1".equals(String.valueOf(activo)));
            alertas.add(dto);
        }

        return alertas;
    }

    private InventarioDto mapearInventario(Map<String, Object> row) {
        InventarioDto dto = new InventarioDto();
        dto.setId(asInt(row.get("id")));
        dto.setProductoId(asInt(row.get("producto_id")));

        String cod = asStr(row.get("producto_codigo"));
        if (cod == null) cod = asStr(row.get("codigo_producto"));
        dto.setProductoCodigo(cod);

        String nom = asStr(row.get("producto_nombre"));
        if (nom == null) nom = asStr(row.get("nombre_producto"));
        dto.setProductoNombre(nom);

        dto.setBodegaId(asInt(row.get("bodega_id")));
        dto.setBodegaNombre(asStr(row.get("bodega_nombre")));
        dto.setCantidadDisponible(asInt(row.get("cantidad_disponible")));
        dto.setCantidadReservada(asInt(row.get("cantidad_reservada")));
        dto.setCantidadTransito(asInt(row.get("cantidad_transito")));
        dto.setStockMinimo(asInt(row.get("stock_minimo")));
        dto.setStockMaximo(asInt(row.get("stock_maximo")));
        dto.setEstadoStock(asStr(row.get("estado_stock")));

        Object ultimaActualizacion = row.get("ultima_actualizacion");
        if (ultimaActualizacion != null) {
            dto.setUltimaActualizacion(parseDateTime(ultimaActualizacion));
        }

        return dto;
    }

    private static Integer asInt(Object o) {
        if (o == null) return null;
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof Number)  return ((Number) o).intValue();
        try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return null; }
    }

    private static String asStr(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static LocalDateTime parseDateTime(Object o) {
        try {
            if (o instanceof LocalDateTime) return (LocalDateTime) o;
            String s = String.valueOf(o);
            if (s.contains("T")) return LocalDateTime.parse(s);
            return LocalDateTime.parse(s.replace(" ", "T"));
        } catch (Exception e) {
            return null;
        }
    }
}
