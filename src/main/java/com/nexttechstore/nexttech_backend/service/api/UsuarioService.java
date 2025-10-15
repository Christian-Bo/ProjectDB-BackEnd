package com.nexttechstore.nexttech_backend.service.api;

import com.nexttechstore.nexttech_backend.dto.seg.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {
    Page<UsuarioDto> listar(String q, Pageable pageable);
    UsuarioDto obtener(Integer id);
    UsuarioDto crear(UsuarioCreateRequest req);
    UsuarioDto actualizar(Integer id, UsuarioUpdateRequest req);
    void eliminar(Integer id);
    UsuarioDto cambiarEstado(Integer id, String estado);
}
