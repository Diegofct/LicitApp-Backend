package com.elemental.licitapp.Seguridad.infrastructure.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Resuelve el id del usuario autenticado para poblar creado_por / actualizado_por.
 *
 * Lee el id del principal (UsuarioAutenticado), que el filtro JWT ya dejo en el
 * SecurityContext a partir del claim uid del token. NO consulta la base de datos, y
 * esa es la decision central de esta clase:
 *
 *   La primera version si consultaba el repositorio para traducir correo -> id, y
 *   provocaba un StackOverflowError. El ciclo era: al hacer commit se dispara
 *   @LastModifiedBy -> AuditorAware consulta -> la consulta JPA fuerza un auto-flush
 *   de los cambios pendientes -> el flush vuelve a disparar el listener de auditoria
 *   -> y otra vez, sin fondo. Cualquier acceso a JPA desde aqui reabre ese ciclo.
 *
 * Devuelve Optional.empty() cuando no hay usuario autenticado (arranque,
 * BootstrapAdminInitializer, tareas de sistema) o cuando el token es anterior al claim
 * uid: en esos casos las columnas quedan NULL, que es lo honesto — no hay autor que
 * registrar. Los tokens viejos empiezan a auditar solos en el siguiente login.
 */
@Component
public class UsuarioAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacion == null || !autenticacion.isAuthenticated()) {
            return Optional.empty();
        }

        if (autenticacion.getPrincipal() instanceof UsuarioAutenticado usuario) {
            return Optional.ofNullable(usuario.id());
        }

        return Optional.empty();
    }
}
