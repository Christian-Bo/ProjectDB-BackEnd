package com.nexttechstore.nexttech_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${app.cors.allowed-origins:http://localhost:*,http://127.0.0.1:*}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS,HEAD}")
    private String[] allowedMethods;

    @Value("${app.cors.allowed-headers:Content-Type,Authorization,Accept,Origin,X-Requested-With,X-User-Id}")
    private String[] allowedHeaders;

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
        log.info("Configuring CORS (WebMvc) pathPattern={} origins={}", pathPattern, String.join(",", allowedOrigins));
        registry.addMapping(pathPattern)
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .exposedHeaders(exposedHeaders)
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }

    /** Filtro extra para asegurar preflights (OPTIONS) con proxies/CDNs. */
    @Bean
    public CorsFilter corsFilterBean() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        cfg.setAllowedMethods(Arrays.asList(allowedMethods));
        cfg.setAllowedHeaders(Arrays.asList(allowedHeaders));
        cfg.setExposedHeaders(Arrays.asList(exposedHeaders));
        cfg.setAllowCredentials(allowCredentials);
        cfg.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(pathPattern, cfg);
        log.info("Configuring CORS (Filter) pathPattern={} origins={}", pathPattern, String.join(",", allowedOrigins));
        return new CorsFilter(source);
    }
}
