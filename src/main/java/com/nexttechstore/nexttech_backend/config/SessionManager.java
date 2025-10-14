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
        public final Integer userId;
        public final Integer rolId;
        public final String rolNombre;
        public OffsetDateTime expiresAt;

        public Session(Integer userId, Integer rolId, String rolNombre, OffsetDateTime expiresAt) {
            this.userId = userId;
            this.rolId = rolId;
            this.rolNombre = rolNombre;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Duration ttl = Duration.ofHours(4);

    public String create(Integer userId, Integer rolId, String rolNombre) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(userId, rolId, rolNombre, OffsetDateTime.now().plus(ttl)));
        return token;
    }

    public Session get(String token) {
        var s = sessions.get(token);
        if (s == null) return null;
        if (s.expiresAt.isBefore(OffsetDateTime.now())) {
            sessions.remove(token);
            return null;
        }
        return s;
    }

    public OffsetDateTime refresh(String token) {
        var s = get(token);
        if (s == null) return null;
        s.expiresAt = OffsetDateTime.now().plus(ttl);
        return s.expiresAt;
    }

    public void invalidate(String token) {
        sessions.remove(token);
    }
}
