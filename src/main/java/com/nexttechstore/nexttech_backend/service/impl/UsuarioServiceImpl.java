package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.seg.*;
import com.nexttechstore.nexttech_backend.model.entity.RolEntity;
import com.nexttechstore.nexttech_backend.model.entity.UsuarioEntity;
import com.nexttechstore.nexttech_backend.repository.orm.EmpleadoJpaRepository;
import com.nexttechstore.nexttech_backend.repository.orm.RolJpaRepository;
import com.nexttechstore.nexttech_backend.repository.orm.UsuarioJpaRepository;
import com.nexttechstore.nexttech_backend.service.api.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioJpaRepository usuarioRepo;
    private final EmpleadoJpaRepository empleadoRepo;
    private final RolJpaRepository rolRepo;
    private final PasswordEncoder passwordEncoder;

    /* ===================== helpers ====================== */

    private UsuarioDto toDto(UsuarioEntity u) {
        String empleadoNombre = null;
        if (u.getEmpleadoId() != null) {
            empleadoNombre = empleadoRepo.findById(u.getEmpleadoId())
                    .map(e -> e.getNombres() + " " + e.getApellidos())
                    .orElse(null);
        }
        Integer rolId = (u.getRol() != null ? u.getRol().getId() : null);
        String rolNombre = (u.getRol() != null ? u.getRol().getNombre() : null);

        return new UsuarioDto(
                u.getId(),
                u.getNombreUsuario(),
                u.getEstado(),
                u.getUltimoAcceso(),
                u.getIntentosFallidos(),
                u.getEmpleadoId(),
                empleadoNombre,
                rolId,
                rolNombre,
                u.getFechaCreacion()
        );
    }

    private RolEntity getRolOrThrow(Integer rolId) {
        return rolRepo.findById(rolId)
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));
    }

    /* ===================== API ====================== */

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDto> listar(String q, Pageable pageable) {
        Page<UsuarioEntity> page = (q != null && !q.isBlank())
                ? usuarioRepo.findByNombreUsuarioContainingIgnoreCase(q.trim(), pageable)
                : usuarioRepo.findAll(pageable);
        return page.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto obtener(Integer id) {
        var u = usuarioRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return toDto(u);
    }

    @Override
    @Transactional
    public UsuarioDto crear(UsuarioCreateRequest req) {
        // username único
        if (usuarioRepo.existsByNombreUsuarioIgnoreCase(req.nombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        // empleado único (1 usuario por empleado)
        if (usuarioRepo.existsByEmpleadoId(req.empleadoId())) {
            throw new IllegalArgumentException("El empleado ya tiene un usuario asignado");
        }
        // validar empleado
        empleadoRepo.findById(req.empleadoId())
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));
        // rol
        var rol = getRolOrThrow(req.rolId());

        var u = UsuarioEntity.builder()
                .nombreUsuario(req.nombreUsuario().trim())
                .password(passwordEncoder.encode(req.password()))
                .estado(req.estado() == null ? "A" : req.estado())
                .ultimoAcceso(null)
                .intentosFallidos(0)
                .fechaCreacion(OffsetDateTime.now())
                .empleadoId(req.empleadoId())
                .rol(rol)
                .build();

        return toDto(usuarioRepo.save(u));
    }

    @Override
    @Transactional
    public UsuarioDto actualizar(Integer id, UsuarioUpdateRequest req) {
        var u = usuarioRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        // username
        if (!u.getNombreUsuario().equalsIgnoreCase(req.nombreUsuario())) {
            if (usuarioRepo.existsByNombreUsuarioIgnoreCase(req.nombreUsuario())) {
                throw new IllegalArgumentException("El nombre de usuario ya existe");
            }
            u.setNombreUsuario(req.nombreUsuario().trim());
        }
        // password (opcional)
        if (req.password() != null && !req.password().isBlank()) {
            u.setPassword(passwordEncoder.encode(req.password()));
        }
        // empleado (unicidad)
        if (!u.getEmpleadoId().equals(req.empleadoId())) {
            if (usuarioRepo.existsByEmpleadoIdAndIdNot(req.empleadoId(), u.getId())) {
                throw new IllegalArgumentException("El empleado ya tiene un usuario asignado");
            }
            // validar empleado
            empleadoRepo.findById(req.empleadoId())
                    .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado"));
            u.setEmpleadoId(req.empleadoId());
        }
        // rol
        if (req.rolId() != null) {
            u.setRol(getRolOrThrow(req.rolId()));
        }
        // estado
        if (req.estado() != null && !req.estado().isBlank()) {
            u.setEstado(req.estado());
        }
        // intentos (opcional)
        if (req.intentosFallidos() != null && req.intentosFallidos() >= 0) {
            u.setIntentosFallidos(req.intentosFallidos());
        }

        return toDto(usuarioRepo.save(u));
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        // SOFT DELETE: estado = 'I' (no borrar físico)
        var u = usuarioRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        u.setEstado("I");
        usuarioRepo.save(u);
    }

    @Override
    @Transactional
    public UsuarioDto cambiarEstado(Integer id, String estado) {
        if (estado == null || !estado.matches("A|I|B")) {
            throw new IllegalArgumentException("Estado inválido (A|I|B)");
        }
        var u = usuarioRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        u.setEstado(estado);
        return toDto(usuarioRepo.save(u));
    }
}
