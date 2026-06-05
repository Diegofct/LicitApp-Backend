package com.elemental.licitapp.Seguridad.infrastructure.out.security;

import com.elemental.licitapp.Seguridad.application.ports.out.HashContrasenaPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptHashAdapter implements HashContrasenaPort {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String hashear(String contrasenaEnClaro) {
        return passwordEncoder.encode(contrasenaEnClaro);
    }

    @Override
    public boolean coincide(String contrasenaEnClaro, String hashAlmacenado) {
        return passwordEncoder.matches(contrasenaEnClaro, hashAlmacenado);
    }
}
