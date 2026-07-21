package com.elemental.licitapp.Seguridad.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuracion central de seguridad: stateless (JWT), CORS desde el bean
 * compartido y matriz de autorizacion por path/rol. La autorizacion se concentra
 * aqui (en vez de @PreAuthorize disperso) para que sea auditable de un vistazo.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CorsConfigurationSource corsConfigurationSource,
                          ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Publico
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()

                        // Gestion de usuarios: solo ADMIN
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        // Operacion del flujo: ANALISTA y ADMIN (incluye escritura de seguimientos)
                        .requestMatchers("/empresas/**", "/cuadro-de-obra/**", "/analisis/**",
                                "/licitaciones/**", "/resultados/**", "/seguimientos/**")
                            .hasAnyRole("ANALISTA", "ADMIN")

                        // Cualquier otra ruta exige autenticacion (ej. /auth/me)
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** 401 en JSON cuando falta o es invalido el token. */
    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) ->
                escribirError(response, HttpServletResponse.SC_UNAUTHORIZED, "No autenticado");
    }

    /** 403 en JSON cuando el rol no tiene permiso. */
    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                escribirError(response, HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
    }

    private void escribirError(HttpServletResponse response, int status, String mensaje) throws java.io.IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("message", mensaje);
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
