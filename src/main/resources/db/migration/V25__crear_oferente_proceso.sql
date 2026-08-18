-- Sobre 2 (oferta economica): oferentes que se presentaron a un proceso.
-- Se alimentan del dataset "SECOPII - Ofertas Por Proceso" (wi7w-2nvm) o a mano, y son la
-- muestra sobre la que se calculan los metodos de ponderacion economica.
--
-- Notas de modelado:
--  * identificador_oferta (CO1.RPL.xxxxxxx) es la clave de deduplicacion: en SECOP una
--    misma oferta genera N filas, una por integrante del consorcio/UT. Es NULL en las
--    altas manuales; MySQL admite multiples NULL en un indice UNIQUE, asi que las altas
--    manuales no colisionan entre si.
--  * nit_oferente queda NULL para consorcios/UT (SECOP publica "No Definido" o
--    "000000000"), asi que el oferente se identifica por nombre.
--  * porcentaje es (valor_oferta / presupuesto_oficial) * 100 truncado a 2 decimales;
--    se guarda como snapshot del momento de la importacion.
--  * valida permite excluir a mano una oferta rechazada por la entidad (p. ej. precio
--    artificialmente bajo) sin borrarla: no entra a las formulas pero queda el rastro.

CREATE TABLE oferente_proceso (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cuadro_de_obra_id BIGINT NOT NULL,
    identificador_oferta VARCHAR(64) NULL,
    referencia_oferta VARCHAR(500) NULL,
    nombre_oferente VARCHAR(500) NOT NULL,
    nit_oferente VARCHAR(32) NULL,
    valor_oferta DECIMAL(20,2) NOT NULL,
    porcentaje DECIMAL(9,2) NULL,
    moneda VARCHAR(16) NULL,
    fecha_registro DATE NULL,
    valida BIT(1) NOT NULL DEFAULT b'1',
    origen VARCHAR(16) NOT NULL,
    fecha_importacion DATETIME(6) NULL,
    fecha_creacion DATETIME(6) NULL,
    fecha_actualizacion DATETIME(6) NULL,
    creado_por BIGINT NULL,
    actualizado_por BIGINT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_oferente_proceso_oferta UNIQUE (cuadro_de_obra_id, identificador_oferta),
    CONSTRAINT fk_oferente_proceso_cuadro FOREIGN KEY (cuadro_de_obra_id)
        REFERENCES cuadro_de_obra (id)
);

-- Inteligencia de competidores: se consulta por nombre a traves de todos los procesos.
CREATE INDEX idx_oferente_proceso_nombre ON oferente_proceso (nombre_oferente);
