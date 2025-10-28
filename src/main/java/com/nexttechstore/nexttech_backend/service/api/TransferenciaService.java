package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.TransferenciaDto;
import java.util.List;

public interface TransferenciaService {
    List<TransferenciaDto> listarTransferencias(Integer bodegaOrigenId, Integer bodegaDestinoId, String estado);
    TransferenciaDto obtenerTransferenciaPorId(Integer id);
    TransferenciaDto crearTransferencia(TransferenciaDto transferenciaDto);
    void aprobarTransferencia(Integer id, Integer aprobadorId);
    void recibirTransferencia(Integer id, Integer receptorId);
    void cancelarTransferencia(Integer id);
}