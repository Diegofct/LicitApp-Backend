package com.elemental.licitapp.Seguridad.infrastructure.config;

import com.elemental.licitapp.Seguridad.application.ports.out.ProveedorTokenPort;
import com.elemental.licitapp.Seguridad.application.ports.out.ProveedorTokenPort.DatosToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Lee el header Authorization: Bearer <token>, valida el JWT a traves del puerto
 * y, si es valido, puebla el SecurityContext con la autenticacion (correo +
 * authority ROLE_<rol>). No emite respuestas de error: si el token falta o es
 * invalido, deja la peticion sin autenticar y la cadena decide (401/403).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIJO = "Bearer ";

    private final ProveedorTokenPort proveedorTokenPort;

    public JwtAuthenticationFilter(ProveedorTokenPort proveedorTokenPort) {
        this.proveedorTokenPort = proveedorTokenPort;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extraerToken(request);
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            proveedorTokenPort.validar(token).ifPresent(datos -> autenticar(datos, request));
        }
        filterChain.doFilter(request, response);
    }

    private void autenticar(DatosToken datos, HttpServletRequest request) {
        var authority = new SimpleGrantedAuthority("ROLE_" + datos.rol());
        var auth = new UsernamePasswordAuthenticationToken(
                datos.correo(), null, List.of(authority));
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER);
        if (header != null && header.startsWith(PREFIJO)) {
            return header.substring(PREFIJO.length()).trim();
        }
        return null;
    }
}
