package com.nexttechstore.nexttech_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexttechstore.nexttech_backend.dto.DetalleTransferenciaDto;
import com.nexttechstore.nexttech_backend.dto.TransferenciaDto;
import com.nexttechstore.nexttech_backend.repository.sp.TransferenciaSpRepository;
import com.nexttechstore.nexttech_backend.service.api.TransferenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TransferenciaServiceImpl implements TransferenciaService {

    @Autowired
    private TransferenciaSpRepository transferenciaSpRepository;

    @Override
    public List<TransferenciaDto> listarTransferencias(Integer bodegaOrigenId, Integer bodegaDestinoId, String estado) {
        List<Map<String, Object>> result = transferenciaSpRepository.listarTransferencias(bodegaOrigenId, bodegaDestinoId, estado);
        List<TransferenciaDto> transferencias = new ArrayList<>();

        for (Map<String, Object> row : result) {
            TransferenciaDto dto = mapearTransferencia(row);
            transferencias.add(dto);
        }

        return transferencias;
    }

    @Override
    public TransferenciaDto obtenerTransferenciaPorId(Integer id) {
        Map<String, Object> row = transferenciaSpRepository.obtenerTransferenciaPorId(id);

        if (row == null) {
            return null;
        }

        TransferenciaDto dto = mapearTransferencia(row);

        List<Map<String, Object>> detalleResult = transferenciaSpRepository.obtenerDetalleTransferencia(id);
        List<DetalleTransferenciaDto> detalles = new ArrayList<>();

        for (Map<String, Object> detRow : detalleResult) {
            DetalleTransferenciaDto detDto = new DetalleTransferenciaDto();
            detDto.setId((Integer) detRow.get("id"));
            detDto.setTransferenciaId((Integer) detRow.get("transferencia_id"));
            detDto.setProductoId((Integer) detRow.get("producto_id"));
            detDto.setProductoCodigo((String) detRow.get("producto_codigo"));
            detDto.setProductoNombre((String) detRow.get("producto_nombre"));
            detDto.setCantidadSolicitada((Integer) detRow.get("cantidad_solicitada"));
            detDto.setCantidadAprobada((Integer) detRow.get("cantidad_aprobada"));
            detDto.setCantidadRecibida((Integer) detRow.get("cantidad_recibida"));
            detDto.setObservaciones((String) detRow.get("observaciones"));
            detalles.add(detDto);
        }

        dto.setDetalles(detalles);

        return dto;
    }

    @Override
    public TransferenciaDto crearTransferencia(TransferenciaDto transferenciaDto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> detallesJson = new ArrayList<>();

            for (DetalleTransferenciaDto det : transferenciaDto.getDetalles()) {
                Map<String, Object> item = Map.of(
                        "producto_id", det.getProductoId(),
                        "cantidad", det.getCantidadSolicitada()
                );
                detallesJson.add(item);
            }

            String detallesJsonString = mapper.writeValueAsString(detallesJson);

            Map<String, Object> result = transferenciaSpRepository.crearTransferencia(
                    transferenciaDto.getNumeroTransferencia(),
                    transferenciaDto.getFechaTransferencia().toString(),
                    transferenciaDto.getBodegaOrigenId(),
                    transferenciaDto.getBodegaDestinoId(),
                    transferenciaDto.getSolicitanteId(),
                    transferenciaDto.getObservaciones(),
                    detallesJsonString
            );

            if (result != null && result.containsKey("id")) {
                Integer newId = ((Number) result.get("id")).intValue();
                return obtenerTransferenciaPorId(newId);
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void aprobarTransferencia(Integer id, Integer aprobadorId) {
        transferenciaSpRepository.aprobarTransferencia(id, aprobadorId);
    }

    @Override
    public void recibirTransferencia(Integer id, Integer receptorId) {
        transferenciaSpRepository.recibirTransferencia(id, receptorId);
    }

    @Override
    public void cancelarTransferencia(Integer id) {
        transferenciaSpRepository.cancelarTransferencia(id);
    }

    private TransferenciaDto mapearTransferencia(Map<String, Object> row) {
        TransferenciaDto dto = new TransferenciaDto();
        dto.setId((Integer) row.get("id"));
        dto.setNumeroTransferencia((String) row.get("numero_transferencia"));

        Object fechaTransferencia = row.get("fecha_transferencia");
        if (fechaTransferencia != null) {
            dto.setFechaTransferencia(LocalDate.parse(fechaTransferencia.toString()));
        }

        dto.setBodegaOrigenId((Integer) row.get("bodega_origen_id"));
        dto.setBodegaOrigenNombre((String) row.get("bodega_origen_nombre"));
        dto.setBodegaDestinoId((Integer) row.get("bodega_destino_id"));
        dto.setBodegaDestinoNombre((String) row.get("bodega_destino_nombre"));
        dto.setEstado((String) row.get("estado"));
        dto.setEstadoDescripcion((String) row.get("estado_descripcion"));
        dto.setSolicitanteId((Integer) row.get("solicitante_id"));
        dto.setAprobadorId((Integer) row.get("aprobador_id"));
        dto.setReceptorId((Integer) row.get("receptor_id"));
        dto.setObservaciones((String) row.get("observaciones"));

        Object fechaAprobacion = row.get("fecha_aprobacion");
        if (fechaAprobacion != null) {
            dto.setFechaAprobacion(LocalDateTime.parse(fechaAprobacion.toString().replace(" ", "T")));
        }

        Object fechaRecepcion = row.get("fecha_recepcion");
        if (fechaRecepcion != null) {
            dto.setFechaRecepcion(LocalDateTime.parse(fechaRecepcion.toString().replace(" ", "T")));
        }

        Object fechaCreacion = row.get("fecha_creacion");
        if (fechaCreacion != null) {
            dto.setFechaCreacion(LocalDateTime.parse(fechaCreacion.toString().replace(" ", "T")));
        }

        return dto;
    }
}