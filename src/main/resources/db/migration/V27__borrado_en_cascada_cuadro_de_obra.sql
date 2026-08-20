-- Borrar un Cuadro de Obra fallaba con 500 en cuanto el proceso hubiera pasado por
-- PRESENTADO: las tres tablas que cuelgan del cuadro tenian la FK sin regla de borrado,
-- asi que MySQL rechazaba el DELETE por integridad referencial.
--
-- Se pasan a ON DELETE CASCADE porque esas filas no tienen vida propia: una conformacion,
-- un seguimiento o los oferentes importados no significan nada sin su cuadro. Dejarlas
-- huerfanas seria peor que borrarlas.
--
-- Los hijos de segundo nivel (integrante_consorcio y evento_seguimiento) ya estaban en
-- CASCADE desde V9 y V10, asi que la cadena completa queda cubierta.
--
-- requisitos_licitacion no se toca: CuadroDeObraService ya lo borra explicitamente antes
-- del cuadro, y ademas su FK la genero Hibernate con un nombre aleatorio distinto en cada
-- entorno (FKfl2ads5ymp0rgcet5p4uxovww aqui), que no se puede referenciar de forma portable.

ALTER TABLE conformacion_consorcio
    DROP FOREIGN KEY fk_conformacion_consorcio_cuadro;
ALTER TABLE conformacion_consorcio
    ADD CONSTRAINT fk_conformacion_consorcio_cuadro FOREIGN KEY (cuadro_de_obra_id)
        REFERENCES cuadro_de_obra (id) ON DELETE CASCADE;

ALTER TABLE seguimiento_proceso
    DROP FOREIGN KEY fk_seguimiento_proceso_cuadro;
ALTER TABLE seguimiento_proceso
    ADD CONSTRAINT fk_seguimiento_proceso_cuadro FOREIGN KEY (cuadro_de_obra_id)
        REFERENCES cuadro_de_obra (id) ON DELETE CASCADE;

ALTER TABLE oferente_proceso
    DROP FOREIGN KEY fk_oferente_proceso_cuadro;
ALTER TABLE oferente_proceso
    ADD CONSTRAINT fk_oferente_proceso_cuadro FOREIGN KEY (cuadro_de_obra_id)
        REFERENCES cuadro_de_obra (id) ON DELETE CASCADE;
