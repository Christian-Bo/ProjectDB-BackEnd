package com.nexttechstore.nexttech_backend.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * Filtro CORS a nivel de servlet (sin Spring Security).
 * Responde SIEMPRE con los headers CORS (incluido en errores y 404/500),
 * y corta el preflight (OPTIONS) devolviendo 200 OK.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsGlobalFilter implements Filter {

    // Orígenes permitidos en desarrollo (ajusta si usas otros)
    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "http://localhost:8082",
            "http://127.0.0.1:8082",
            "http://localhost:8080",
            "http://127.0.0.1:8080"
    );

    @Override
    public void init(FilterConfig filterConfig) { /* no-op */ }

    @Override
    public void destroy() { /* no-op */ }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            // Headers CORS base
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin"); // evita cache mala entre orígenes
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,Accept,Origin,X-Requested-With,X-User-Id");
            response.setHeader("Access-Control-Expose-Headers", "Location,Link");
            response.setHeader("Access-Control-Max-Age", "3600");
            // Si compartirás cookies entre dominios:
            // response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        // Responder el preflight sin pasar al resto de filtros/controladores
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
}
