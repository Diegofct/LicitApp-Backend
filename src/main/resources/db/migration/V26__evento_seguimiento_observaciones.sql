-- Observaciones libres de un evento de seguimiento.
-- El frontend ya pedia este campo en el modal de registro y lo pintaba en la linea de
-- tiempo, pero el backend nunca lo acepto: RegistrarEventoRequestDTO solo tenia tipo,
-- fechaEvento y descripcion, asi que lo que el usuario escribia se descartaba en silencio.
-- Es opcional: distingue el "que paso" (descripcion) del "que anotamos al respecto".
--
-- IDEMPOTENTE a proposito. En este proyecto Flyway convive con ddl-auto=update, y el
-- despliegue arranca en dos fases (ver .env.example de licitapp-deploy): en una BD nueva
-- Hibernate crea el esquema primero y ya habria anadido esta columna al leer la entidad.
-- Volver a anadirla daria "Duplicate column" y dejaria el backend sin arrancar.
-- MySQL no soporta ADD COLUMN IF NOT EXISTS, de ahi el SQL dinamico.

SET @existe_columna := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'evento_seguimiento'
      AND COLUMN_NAME = 'observaciones'
);

SET @sql := IF(@existe_columna = 0,
    'ALTER TABLE evento_seguimiento ADD COLUMN observaciones TEXT NULL AFTER descripcion',
    'DO 0');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
