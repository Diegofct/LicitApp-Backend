-- Tabla de usuarios para el modulo Seguridad. Autenticacion stateless con JWT.
-- El correo es el identificador de acceso (unico). La contrasena se guarda
-- hasheada con BCrypt: nunca se almacena ni se expone en texto plano.
-- Roles: ANALISTA (la licitadora), PROPIETARIO (solo lectura), ADMIN (gestion).
-- El alta de usuarios la realiza unicamente un ADMIN (no hay auto-registro).

CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    correo VARCHAR(180) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    rol VARCHAR(32) NOT NULL,
    activo BIT(1) NOT NULL DEFAULT b'1',
    fecha_creacion DATETIME(6) NOT NULL,
    fecha_actualizacion DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_usuarios_correo UNIQUE (correo)
);
