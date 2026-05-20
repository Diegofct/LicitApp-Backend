-- Tablas para persistir la conformacion de un proponente (individual / consorcio / UT)
-- para un cuadro de obra. Resultado del modulo AnalisisDeCumplimiento.
-- Invariante 1:1 con cuadro_de_obra: cada cuadro tiene a lo sumo una conformacion.

CREATE TABLE conformacion_consorcio (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cuadro_de_obra_id BIGINT NOT NULL,
    tipo_participacion VARCHAR(32) NOT NULL,
    fecha_conformacion DATETIME(6) NOT NULL,
    observaciones TEXT,
    PRIMARY KEY (id),
    CONSTRAINT uq_conformacion_consorcio_cuadro UNIQUE (cuadro_de_obra_id),
    CONSTRAINT fk_conformacion_consorcio_cuadro FOREIGN KEY (cuadro_de_obra_id)
        REFERENCES cuadro_de_obra (id)
);

CREATE TABLE integrante_consorcio (
    id BIGINT NOT NULL AUTO_INCREMENT,
    conformacion_id BIGINT NOT NULL,
    empresa_id BIGINT NOT NULL,
    porcentaje_participacion DECIMAL(5,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_integrante_consorcio_conformacion FOREIGN KEY (conformacion_id)
        REFERENCES conformacion_consorcio (id) ON DELETE CASCADE,
    CONSTRAINT fk_integrante_consorcio_empresa FOREIGN KEY (empresa_id)
        REFERENCES empresas (id),
    CONSTRAINT uq_integrante_consorcio_empresa UNIQUE (conformacion_id, empresa_id)
);
