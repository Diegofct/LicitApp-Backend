package com.elemental.licitapp.Seguridad.application.service;

import com.elemental.licitapp.Exception.CredencialesInvalidasException;
import com.elemental.licitapp.Seguridad.application.ports.in.AutenticarUsuarioUseCase;
import com.elemental.licitapp.Seguridad.application.ports.out.HashContrasenaPort;
import com.elemental.licitapp.Seguridad.application.ports.out.ProveedorTokenPort;
import com.elemental.licitapp.Seguridad.application.ports.out.UsuarioRepositoryPort;
import com.elemental.licitapp.Seguridad.domain.entity.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacionService implements AutenticarUsuarioUseCase {

    private static final String MENSAJE_GENERICO = "Correo o contrasena incorrectos";

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final HashContrasenaPort hashContrasenaPort;
    private final ProveedorTokenPort proveedorTokenPort;

    public AutenticacionService(UsuarioRepositoryPort usuarioRepositoryPort,
                                HashContrasenaPort hashContrasenaPort,
                                ProveedorTokenPort proveedorTokenPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.hashContrasenaPort = hashContrasenaPort;
        this.proveedorTokenPort = proveedorTokenPort;
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoAutenticacion autenticar(String correo, String contrasenaEnClaro) {
        String correoNormalizado = correo == null ? null : correo.trim().toLowerCase();

        Usuario usuario = usuarioRepositoryPort.buscarPorCorreo(correoNormalizado)
                // Mensaje generico: no revelamos si fallo el correo o la contrasena.
                .orElseThrow(() -> new CredencialesInvalidasException(MENSAJE_GENERICO));

        if (!hashContrasenaPort.coincide(contrasenaEnClaro, usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException(MENSAJE_GENERICO);
        }

        if (!usuario.isActivo()) {
            throw new CredencialesInvalidasException("El usuario esta inactivo");
        }

        String token = proveedorTokenPort.generar(usuario);
        return new ResultadoAutenticacion(
                token,
                proveedorTokenPort.expiracionEnSegundos(),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().name()
        );
    }
}
