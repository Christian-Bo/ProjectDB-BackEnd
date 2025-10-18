package com.nexttechstore.nexttech_backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// SessionManager en com...config
import com.nexttechstore.nexttech_backend.config.SessionManager;

@Component
public class RoleGuardInterceptor implements HandlerInterceptor {

    private final SessionManager sessionManager;

    public RoleGuardInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod hm)) return true;

        String path = request.getRequestURI();
        if (path.startsWith("/api/auth")) return true;

        AllowedRoles ann = hm.getMethodAnnotation(AllowedRoles.class);
        if (ann == null) ann = hm.getBeanType().getAnnotation(AllowedRoles.class);

        var required = new HashSet<String>();
        if (ann != null) required.addAll(Arrays.asList(ann.value()));
        if (required.isEmpty()) return true;

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.toLowerCase().startsWith("bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Falta Authorization Bearer");
            return false;
        }
        String token = auth.substring(7).trim();

        var session = sessionManager.get(token);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
            return false;
        }

        OffsetDateTime exp = session.getExpiresAt();
        if (exp != null && OffsetDateTime.now().isAfter(exp)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");
            return false;
        }

        String rolNombre = session.getRolNombre();
        if (rolNombre == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Rol no asignado");
            return false;
        }

        String role = rolNombre.trim().toUpperCase();
        if (role.equals("ADMIN")) return true;

        if (!required.contains(role)) {
            String m = request.getMethod();
            if (role.equals("AUDITOR") && (m.equals("GET") || m.equals("HEAD") || m.equals("OPTIONS"))) {
                return true;
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado para el rol " + rolNombre);
            return false;
        }
        return true;
    }
}
