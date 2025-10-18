package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.*;
import com.nexttechstore.nexttech_backend.model.entity.UsuarioEntity;
import com.nexttechstore.nexttech_backend.repository.orm.UsuarioJpaRepository;
import com.nexttechstore.nexttech_backend.config.SessionManager;
import com.nexttechstore.nexttech_backend.service.api.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioJpaRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final SessionManager sessionManager;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        var user = usuarioRepo.findByNombreUsuario(request.nombreUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña inválidos"));

        if (!"A".equalsIgnoreCase(user.getEstado())) {
            throw new IllegalStateException("Usuario inactivo/bloqueado");
        }

        // Validación de password (se asume BCrypt en BD)
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            user.setIntentosFallidos(user.getIntentosFallidos() + 1);
            usuarioRepo.save(user);
            throw new IllegalArgumentException("Usuario o contraseña inválidos");
        }

        user.setIntentosFallidos(0);
        user.setUltimoAcceso(OffsetDateTime.now());
        usuarioRepo.save(user);

        String token = sessionManager.create(user.getId(), user.getRol().getId(), user.getRol().getNombre());
        var session = sessionManager.get(token);

        var dto = new UserDto(
                user.getId(),
                user.getNombreUsuario(),
                user.getEstado(),
                user.getEmpleadoId(),
                user.getRol().getId(),
                user.getRol().getNombre()
        );
        // 👇 usar getter de expiresAt
        return new LoginResponse(token, session.getExpiresAt(), dto);
    }

    @Override
    public LoginResponse refresh(RefreshRequest request) {
        var newExp = sessionManager.refresh(request.token());
        if (newExp == null) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }
        var s = sessionManager.get(request.token());
        // 👇 usar getters de rol
        var dto = new UserDto(null, null, null, null, s.getRolId(), s.getRolNombre());
        return new LoginResponse(request.token(), newExp, dto);
    }

    @Override
    public void logout(LogoutRequest request) {
        sessionManager.invalidate(request.token());
    }

    @Override
    public UserDto me(String bearerToken) {
        // Formato esperado: "Bearer <token>"
        if (bearerToken == null || !bearerToken.toLowerCase().startsWith("bearer ")) {
            throw new IllegalArgumentException("Falta Authorization Bearer");
        }
        String token = bearerToken.substring(7).trim();
        var s = sessionManager.get(token);
        if (s == null) throw new IllegalArgumentException("Token inválido o expirado");

        // 👇 usar getters de session
        return new UserDto(s.getUserId(), null, "A", null, s.getRolId(), s.getRolNombre());
    }
}
