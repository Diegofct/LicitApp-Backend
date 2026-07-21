package com.elemental.licitapp.Seguridad.infrastructure.out.security;

import com.elemental.licitapp.Seguridad.application.ports.out.ProveedorTokenPort;
import com.elemental.licitapp.Seguridad.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtProveedorTokenAdapter implements ProveedorTokenPort {

    private static final Logger log = LoggerFactory.getLogger(JwtProveedorTokenAdapter.class);
    private static final String CLAIM_ROL = "rol";
    private static final String CLAIM_UID = "uid";

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtProveedorTokenAdapter(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expiracionMs) {
        // La firma usa HMAC-SHA256: el secreto debe tener al menos 32 bytes.
        this.clave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    @Override
    public String generar(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim(CLAIM_ROL, usuario.getRol().name())
                .claim(CLAIM_UID, usuario.getId())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusMillis(expiracionMs)))
                .signWith(clave)
                .compact();
    }

    @Override
    public long expiracionEnSegundos() {
        return expiracionMs / 1000;
    }

    @Override
    public Optional<DatosToken> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(new DatosToken(
                    extraerUid(claims),
                    claims.getSubject(),
                    claims.get(CLAIM_ROL, String.class)));
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Token JWT invalido: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Lee el claim uid como Number en vez de Long: el JSON del token deserializa los
     * enteros pequenos como Integer, y pedir Long directamente falla. Devuelve null
     * en tokens emitidos antes de que existiera el claim (siguen siendo validos).
     */
    private Long extraerUid(Claims claims) {
        Number uid = claims.get(CLAIM_UID, Number.class);
        return uid == null ? null : uid.longValue();
    }
}
