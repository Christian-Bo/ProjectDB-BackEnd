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
 * ✔ Incluye el frontend en 8082
 * ✔ Permite header X-User-Id (y otros comunes)
 * ✔ Responde preflight (OPTIONS)
 * ✔ Path /api/** (ajústalo si usas otro)
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    // IMPORTANTE: agrega 8082 y 127.0.0.1:8082
    @Value("${app.cors.allowed-origins:http://localhost:8080,http://127.0.0.1:8080,http://localhost:8082,http://127.0.0.1:8082}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    // IMPORTANTE: agrega X-User-Id y Accept/Origin
    @Value("${app.cors.allowed-headers:Content-Type,Authorization,Accept,Origin,X-Requested-With,X-User-Id}")
    private String[] allowedHeaders;

    // (Opcional) headers que el browser podrá leer si los envías
    @Value("${app.cors.exposed-headers:Location,Link}")
    private String[] exposedHeaders;

    @Value("${app.cors.allow-credentials:false}")
    private boolean allowCredentials;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Value("${app.cors.path-pattern:/api/**}")
    private String pathPattern;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configuring CORS for pathPattern={} origins={}", pathPattern, String.join(",", allowedOrigins));

        registry.addMapping(pathPattern)
                .allowedOrigins(allowedOrigins)          // añade http://localhost:8082
                .allowedMethods(allowedMethods)          // incluye OPTIONS
                .allowedHeaders(allowedHeaders)          // incluye X-User-Id
                .exposedHeaders(exposedHeaders)          // opcional
                .allowCredentials(allowCredentials)      // true solo si compartes cookies entre dominios
                .maxAge(maxAge);
    }
}
