package com.elemental.licitapp.Seguridad.infrastructure.config;

import java.security.Principal;

/**
 * Principal que el JwtAuthenticationFilter deja en el SecurityContext.
 *
 * Implementa java.security.Principal a proposito: UsernamePasswordAuthenticationToken
 * delega getName() en el principal cuando este es un Principal, asi que
 * authentication.getName() sigue devolviendo el correo y los consumidores existentes
 * (AuthController, UsuarioController) no cambian.
 *
 * Lo que aporta sobre el String pelado de antes es el id, que UsuarioAuditorAware
 * necesita para poblar creado_por / actualizado_por sin ir a la BD.
 *
 * id es null si el token se emitio antes de que existiera el claim uid.
 */
public record UsuarioAutenticado(Long id, String correo) implements Principal {

    @Override
    public String getName() {
        return correo;
    }
}
