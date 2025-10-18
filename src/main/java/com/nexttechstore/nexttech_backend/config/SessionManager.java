package com.nexttechstore.nexttech_backend.config;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    public static class Session {
        private final Integer userId;
        private final Integer rolId;
        private final String rolNombre;
        private OffsetDateTime expiresAt;

        public Session(Integer userId, Integer rolId, String rolNombre, OffsetDateTime expiresAt) {
            this.userId = userId;
            this.rolId = rolId;
            this.rolNombre = rolNombre;
            this.expiresAt = expiresAt;
        }

        // ==== GETTERS necesarios para el interceptor ====
        public Integer getUserId() { return userId; }
        public Integer getRolId() { return rolId; }
        public String getRolNombre() { return rolNombre; }
        public OffsetDateTime getExpiresAt() { return expiresAt; }

        // (setter solo para refresh interno)
        public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Duration ttl = Duration.ofHours(4);

    /** Crea una nueva sesión y retorna el token */
    public String create(Integer userId, Integer rolId, String rolNombre) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(userId, rolId, rolNombre, OffsetDateTime.now().plus(ttl)));
        return token;
    }

    /** Obtiene la sesión válida por token (o null si no existe / expiró) */
    public Session get(String token) {
        var s = sessions.get(token);
        if (s == null) return null;
        if (s.getExpiresAt().isBefore(OffsetDateTime.now())) {
            sessions.remove(token);
            return null;
        }
        return s;
    }

    /** Extiende la vida de la sesión y retorna la nueva fecha de expiración (o null si no existe) */
    public OffsetDateTime refresh(String token) {
        var s = get(token);
        if (s == null) return null;
        s.setExpiresAt(OffsetDateTime.now().plus(ttl));
        return s.getExpiresAt();
    }

    /** Invalida la sesión (logout) */
    public void invalidate(String token) {
        sessions.remove(token);
    }
}
