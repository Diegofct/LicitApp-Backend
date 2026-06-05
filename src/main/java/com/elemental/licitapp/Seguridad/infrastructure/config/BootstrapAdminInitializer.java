package com.elemental.licitapp.Seguridad.infrastructure.config;

import com.elemental.licitapp.Seguridad.application.ports.in.RegistrarUsuarioUseCase;
import com.elemental.licitapp.Seguridad.application.ports.out.UsuarioRepositoryPort;
import com.elemental.licitapp.Seguridad.domain.enums.Rol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Crea el primer usuario ADMIN si todavia no existe ninguno. Resuelve el
 * huevo-gallina del alta solo-ADMIN. Las credenciales se leen de variables de
 * entorno (no se hardcodean en el repo). Si ya hay un ADMIN, no hace nada.
 *
 * Variables: ADMIN_BOOTSTRAP_ENABLED, ADMIN_CORREO, ADMIN_PASSWORD, ADMIN_NOMBRE.
 */
@Component
public class BootstrapAdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminInitializer.class);

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final boolean habilitado;
    private final String correo;
    private final String password;
    private final String nombre;

    public BootstrapAdminInitializer(
            UsuarioRepositoryPort usuarioRepositoryPort,
            RegistrarUsuarioUseCase registrarUsuarioUseCase,
            @org.springframework.beans.factory.annotation.Value("${app.admin.bootstrap-enabled:false}") boolean habilitado,
            @org.springframework.beans.factory.annotation.Value("${app.admin.correo:}") String correo,
            @org.springframework.beans.factory.annotation.Value("${app.admin.password:}") String password,
            @org.springframework.beans.factory.annotation.Value("${app.admin.nombre:Administrador}") String nombre) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.habilitado = habilitado;
        this.correo = correo;
        this.password = password;
        this.nombre = nombre;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }
        if (usuarioRepositoryPort.existeAlgunAdmin()) {
            log.info("Bootstrap admin omitido: ya existe al menos un ADMIN.");
            return;
        }
        if (correo == null || correo.isBlank() || password == null || password.isBlank()) {
            log.warn("Bootstrap admin habilitado pero faltan ADMIN_CORREO/ADMIN_PASSWORD. No se crea el admin.");
            return;
        }
        registrarUsuarioUseCase.registrar(nombre, correo, password, Rol.ADMIN);
        log.warn("Usuario ADMIN inicial creado para '{}'. Cambia la contrasena y desactiva ADMIN_BOOTSTRAP_ENABLED.", correo);
    }
}
