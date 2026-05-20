-- Tablas para el modulo SeguimientoProceso. Ciclo de vida post-presentacion de
-- un cuadro de obra: eventos que el usuario va añadiendo para hacerle seguimiento
-- al proceso publicado en SECOP II (subsanaciones, informes, audiencias, etc.).
-- Invariante 1:1 con cuadro_de_obra: a lo sumo un seguimiento por cuadro.

CREATE TABLE seguimiento_proceso (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cuadro_de_obra_id BIGINT NOT NULL,
    fecha_inicio DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_seguimiento_proceso_cuadro UNIQUE (cuadro_de_obra_id),
    CONSTRAINT fk_seguimiento_proceso_cuadro FOREIGN KEY (cuadro_de_obra_id)
        REFERENCES cuadro_de_obra (id)
);

CREATE TABLE evento_seguimiento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    seguimiento_id BIGINT NOT NULL,
    tipo VARCHAR(64) NOT NULL,
    fecha_evento DATETIME(6) NOT NULL,
    descripcion TEXT,
    fecha_registro DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_evento_seguimiento_seguimiento FOREIGN KEY (seguimiento_id)
        REFERENCES seguimiento_proceso (id) ON DELETE CASCADE
);

CREATE INDEX idx_evento_seguimiento_seguimiento ON evento_seguimiento (seguimiento_id);
