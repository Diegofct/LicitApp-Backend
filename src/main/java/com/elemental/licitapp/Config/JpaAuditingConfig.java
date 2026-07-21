package com.elemental.licitapp.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Activa la auditoria de autoria de Spring Data JPA (@CreatedBy / @LastModifiedBy /
 * @CreatedDate / @LastModifiedDate).
 *
 * Vive en Config/ porque es infraestructura transversal: afecta a entidades de varios
 * slices (CuadroDeObra, AnalisisDeCumplimiento, SeguimientoProceso). Quien resuelve
 * "que usuario es el actual" si es responsabilidad de Seguridad, y por eso el
 * AuditorAware se inyecta desde alli por nombre de bean.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "usuarioAuditorAware")
public class JpaAuditingConfig {
}
