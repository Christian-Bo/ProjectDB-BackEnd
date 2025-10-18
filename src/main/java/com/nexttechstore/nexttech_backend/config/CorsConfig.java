package com.nexttechstore.nexttech_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS GLOBAL para toda la API.
 *
 * - Usa allowedOriginPatterns para soportar puertos variables en dev (http://localhost:*, http://127.0.0.1:*)
 * - Permite métodos comunes + PATCH y HEAD
 * - Incluye headers típicos de fetch/axios y un custom (X-User-Id)
 * - Responde preflight (OPTIONS) y expone headers útiles (Location, Link)
 * - Se aplica a /api/** (ajústalo si usas otro prefijo)
 *
 * NOTA (si usas Spring Security):
 *   agrega en tu SecurityFilterChain -> http.cors(cors -> {});
 *   para que esta config WebMvcConfigurer tome efecto.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    /**
     * Patrones de orígenes permitidos.
     * Ejemplos:
     *  - http://localhost:* (cubre 3000, 5173, 8082, etc.)
     *  - http://127.0.0.1:*
     *  - También puedes listar orígenes fijos: http://mi-front.local:8080
     */
    @Value("${app.cors.allowed-origins:http://localhost:*,http://127.0.0.1:*}")
    private String[] allowedOrigins;

    /** Métodos permitidos: incluye PATCH y HEAD además de OPTIONS para preflight. */
    @Value("${app.cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS,HEAD}")
    private String[] allowedMethods;

    /** Headers aceptados desde el frontend (incluye X-User-Id y cabeceras típicas). */
    @Value("${app.cors.allowed-headers:Content-Type,Authorization,Accept,Origin,X-Requested-With,X-User-Id}")
    private String[] allowedHeaders;

    /** Headers que el navegador podrá leer si los envías (útil para 201 Created -> Location). */
    @Value("${app.cors.exposed-headers:Location,Link}")
    private String[] exposedHeaders;

    /** ¿Compartir cookies/sesión entre orígenes? TRUE solo si realmente las usas cross-site. */
    @Value("${app.cors.allow-credentials:false}")
    private boolean allowCredentials;

    /** Cache del preflight en el navegador (segundos). */
    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    /** A qué paths aplica CORS (por defecto a todo tu API). */
    @Value("${app.cors.path-pattern:/api/**}")
    private String pathPattern;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configuring CORS pathPattern={} origins={}", pathPattern, String.join(",", allowedOrigins));

        registry.addMapping(pathPattern)
                // IMPORTANTE: usamos patterns para soportar puertos variables en dev
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .exposedHeaders(exposedHeaders)
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }
}
