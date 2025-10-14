// config/PasswordConfig.java
package com.nexttechstore.nexttech_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        // SOLO PARA PRUEBAS: compara texto plano
        return new PasswordEncoder() {
            @Override public String encode(CharSequence raw) { return raw.toString(); }
            @Override public boolean matches(CharSequence raw, String encoded) {
                return encoded != null && encoded.equals(raw.toString());
            }
        };
    }
}
