package com.nexttechstore.nexttech_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexttechstore.nexttech_backend.dto.AjusteInventarioDto;
import com.nexttechstore.nexttech_backend.dto.DetalleAjusteDto;
import com.nexttechstore.nexttech_backend.repository.sp.AjusteInventarioSpRepository;
import com.nexttechstore.nexttech_backend.service.api.AjusteInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AjusteInventarioServiceImpl implements AjusteInventarioService {

    @Autowired
    private AjusteInventarioSpRepository ajusteInventarioSpRepository;

    @Override
    public List<AjusteInventarioDto> listarAjustes(Integer bodegaId, String tipoAjuste,
                                                   String fechaDesde, String fechaHasta) {
        List<Map<String, Object>> result = ajusteInventarioSpRepository.listarAjustes(
                bodegaId, tipoAjuste, fechaDesde, fechaHasta);
        List<AjusteInventarioDto> ajustes = new ArrayList<>();

        for (Map<String, Object> row : result) {
            AjusteInventarioDto dto = mapearAjuste(row);
            ajustes.add(dto);
        }

        return ajustes;
    }

    @Override
    public AjusteInventarioDto obtenerAjustePorId(Integer id) {
        Map<String, Object> row = ajusteInventarioSpRepository.obtenerAjustePorId(id);

        if (row == null) {
            return null;
        }

        AjusteInventarioDto dto = mapearAjuste(row);

        List<Map<String, Object>> detalleResult = ajusteInventarioSpRepository.obtenerDetalleAjuste(id);
        List<DetalleAjusteDto> detalles = new ArrayList<>();

        for (Map<String, Object> detRow : detalleResult) {
            DetalleAjusteDto detDto = new DetalleAjusteDto();
            detDto.setId((Integer) detRow.get("id"));
            detDto.setAjusteId((Integer) detRow.get("ajuste_id"));
            detDto.setProductoId((Integer) detRow.get("producto_id"));
            detDto.setProductoCodigo((String) detRow.get("producto_codigo"));
            detDto.setProductoNombre((String) detRow.get("producto_nombre"));
            detDto.setCantidadAntes((Integer) detRow.get("cantidad_anterior"));
            detDto.setCantidadAjuste((Integer) detRow.get("cantidad_ajuste"));
            detDto.setCantidadDespues((Integer) detRow.get("cantidad_nueva"));

            Object costo = detRow.get("costo_unitario");
            if (costo != null) {
                detDto.setCostoUnitario(new BigDecimal(costo.toString()));
            }

            detDto.setObservaciones((String) detRow.get("observaciones"));
            detalles.add(detDto);
        }

        dto.setDetalles(detalles);

        return dto;
    }

    @Override
    public AjusteInventarioDto crearAjuste(AjusteInventarioDto ajusteDto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> detallesJson = new ArrayList<>();

            for (DetalleAjusteDto det : ajusteDto.getDetalles()) {
                Map<String, Object> item = Map.of(
                        "producto_id", det.getProductoId(),
                        "cantidad", det.getCantidadAjuste(),
                        "costo", det.getCostoUnitario() != null ? det.getCostoUnitario() : 0
                );
                detallesJson.add(item);
            }

            String detallesJsonString = mapper.writeValueAsString(detallesJson);

            Map<String, Object> result = ajusteInventarioSpRepository.crearAjuste(
                    ajusteDto.getNumeroAjuste(),
                    ajusteDto.getFechaAjuste().toString(),
                    ajusteDto.getBodegaId(),
                    ajusteDto.getTipoAjuste(),
                    ajusteDto.getMotivo(),
                    ajusteDto.getResponsableId(),
                    ajusteDto.getObservaciones(),
                    detallesJsonString
            );

            if (result != null && result.containsKey("id")) {
                Integer newId = ((Number) result.get("id")).intValue();
                return obtenerAjustePorId(newId);
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private AjusteInventarioDto mapearAjuste(Map<String, Object> row) {
        AjusteInventarioDto dto = new AjusteInventarioDto();
        dto.setId((Integer) row.get("id"));
        dto.setNumeroAjuste((String) row.get("numero_ajuste"));

        Object fechaAjuste = row.get("fecha_ajuste");
        if (fechaAjuste != null) {
            dto.setFechaAjuste(LocalDate.parse(fechaAjuste.toString()));
        }

        dto.setBodegaId((Integer) row.get("bodega_id"));
        dto.setBodegaNombre((String) row.get("bodega_nombre"));
        dto.setTipoAjuste((String) row.get("tipo_ajuste"));
        dto.setTipoAjusteDescripcion((String) row.get("tipo_ajuste_descripcion"));
        dto.setMotivo((String) row.get("motivo"));
        dto.setResponsableId((Integer) row.get("responsable_id"));
        dto.setResponsableNombre((String) row.get("responsable_nombre"));
        dto.setObservaciones((String) row.get("observaciones"));

        Object fechaCreacion = row.get("fecha_creacion");
        if (fechaCreacion != null) {
            dto.setFechaCreacion(LocalDateTime.parse(fechaCreacion.toString().replace(" ", "T")));
        }

        return dto;
    }
}