package com.elemental.licitapp.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS centralizado como CorsConfigurationSource para que lo consuma la cadena
 * de Spring Security (SecurityConfig -> .cors(...)). Con Spring Security activo,
 * el CorsFilter de la cadena es quien resuelve el preflight; por eso ya no se
 * usa un WebMvcConfigurer aqui.
 *
 * En produccion el SPA y la API se sirven desde el MISMO origen (Caddy sirve los
 * estaticos y hace de proxy a /api/*), pero eso NO exime de configurar esto: el
 * navegador manda la cabecera Origin en todo POST/PUT/DELETE aunque sean del mismo
 * origen, y Spring rechaza con 403 cualquier Origin que no figure en la lista. De
 * ahi que los origenes sean configurables por entorno (CORS_ALLOWED_ORIGINS): el
 * dominio de produccion tiene que estar ahi o el login devuelve 403.
 */
@Configuration
public class CorsConfig {

    private final List<String> origenesPermitidos;

    public CorsConfig(@Value("${licitapp.cors.allowed-origins}") List<String> origenesPermitidos) {
        // Se descartan los blancos para tolerar una variable de entorno vacia o con comas
        // de sobra: un origen "" haria que Spring rechazara absolutamente todo con 403.
        this.origenesPermitidos = origenesPermitidos.stream()
                .map(String::trim)
                .filter(origen -> !origen.isEmpty())
                .toList();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenesPermitidos);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
