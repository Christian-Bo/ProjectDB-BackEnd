package com.nexttechstore.nexttech_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración CORS GLOBAL (aplica a todo el backend).
 *
 * - Lee orígenes/métodos/headers desde application.properties (ver sección 2).
 * - Cubre también las peticiones preflight (OPTIONS).
 * - No requiere @CrossOrigin en controladores.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:8080,http://127.0.0.1:8080}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${app.cors.allowed-headers:Content-Type,Authorization}")
    private String[] allowedHeaders;

    @Value("${app.cors.allow-credentials:false}")
    private boolean allowCredentials;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Value("${app.cors.path-pattern:/api/**}")
    private String pathPattern;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(pathPattern)
                .allowedOrigins(allowedOrigins)     // p.ej. http://localhost:8080 (tu JSP)
                .allowedMethods(allowedMethods)     // incluye OPTIONS para preflight
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials) // true solo si compartes cookies/sesión entre orígenes
                .maxAge(maxAge);                    // cache del preflight en segundos
    }
}
