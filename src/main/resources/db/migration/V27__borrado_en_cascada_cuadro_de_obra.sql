-- Borrar un Cuadro de Obra fallaba con 500 en cuanto el proceso hubiera pasado por
-- PRESENTADO: las tablas que cuelgan del cuadro tenian la FK sin regla de borrado, asi
-- que MySQL rechazaba el DELETE por integridad referencial.
--
-- Se pasan a ON DELETE CASCADE porque esas filas no tienen vida propia: una conformacion,
-- un seguimiento o los oferentes importados no significan nada sin su cuadro. Dejarlas
-- huerfanas seria peor que borrarlas. Los hijos de segundo nivel (integrante_consorcio y
-- evento_seguimiento) ya estaban en CASCADE desde V9 y V10, asi que la cadena queda entera.
--
-- IDEMPOTENTE y sin nombres de constraint fijos, a proposito. Estas tres entidades guardan
-- el cuadro como un Long plano, SIN @ManyToOne, asi que cuando el esquema lo crea Hibernate
-- (fase 1 del despliegue) estas FK sencillamente NO EXISTEN; y cuando lo crea Flyway existen
-- con los nombres de V9/V10/V25. Un DROP FOREIGN KEY con nombre fijo reventaria en el primer
-- caso. Por eso se busca el nombre real en information_schema y se omite el DROP si no hay.
--
-- requisitos_licitacion no se toca: CuadroDeObraService ya lo borra explicitamente antes del
-- cuadro. Es la unica de las cuatro que si tiene @OneToOne, asi que su FK la crea Hibernate
-- con un nombre aleatorio distinto en cada entorno.

-- ---------------------------------------------------------------- conformacion_consorcio
SET @fk := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'conformacion_consorcio'
              AND COLUMN_NAME = 'cuadro_de_obra_id' AND REFERENCED_TABLE_NAME = 'cuadro_de_obra'
            LIMIT 1);
SET @sql := IF(@fk IS NULL, 'DO 0',
               CONCAT('ALTER TABLE conformacion_consorcio DROP FOREIGN KEY ', @fk));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE conformacion_consorcio
    ADD CONSTRAINT fk_conformacion_consorcio_cuadro FOREIGN KEY (cuadro_de_obra_id)
        REFERENCES cuadro_de_obra (id) ON DELETE CASCADE;

-- ----------------------------------------------------------------- seguimiento_proceso
SET @fk := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'seguimiento_proceso'
              AND COLUMN_NAME = 'cuadro_de_obra_id' AND REFERENCED_TABLE_NAME = 'cuadro_de_obra'
            LIMIT 1);
SET @sql := IF(@fk IS NULL, 'DO 0',
               CONCAT('ALTER TABLE seguimiento_proceso DROP FOREIGN KEY ', @fk));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE seguimiento_proceso
    ADD CONSTRAINT fk_seguimiento_proceso_cuadro FOREIGN KEY (cuadro_de_obra_id)
        REFERENCES cuadro_de_obra (id) ON DELETE CASCADE;

-- ------------------------------------------------------------------- oferente_proceso
SET @fk := (SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oferente_proceso'
              AND COLUMN_NAME = 'cuadro_de_obra_id' AND REFERENCED_TABLE_NAME = 'cuadro_de_obra'
            LIMIT 1);
SET @sql := IF(@fk IS NULL, 'DO 0',
               CONCAT('ALTER TABLE oferente_proceso DROP FOREIGN KEY ', @fk));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE oferente_proceso
    ADD CONSTRAINT fk_oferente_proceso_cuadro FOREIGN KEY (cuadro_de_obra_id)
        REFERENCES cuadro_de_obra (id) ON DELETE CASCADE;
