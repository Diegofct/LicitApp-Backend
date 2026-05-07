-- Hibernate (ddl-auto=update) no puede convertir la columna codigos_unspsc de su tipo actual
-- (longtext/blob con charset binary) a JSON mediante un ALTER directo.
-- Esta migracion hace la conversion de forma segura usando una columna intermedia.

ALTER TABLE experiencia_empresa
  ADD COLUMN codigos_unspsc_new JSON NULL;

-- Copia los datos existentes. Si la columna actual ya contiene JSON valido como texto,
-- CAST lo convierte; si es NULL o vacio, queda NULL en la nueva.
UPDATE experiencia_empresa
SET codigos_unspsc_new = CASE
    WHEN codigos_unspsc IS NULL OR CHAR_LENGTH(codigos_unspsc) = 0 THEN NULL
    WHEN JSON_VALID(CONVERT(codigos_unspsc USING utf8mb4)) THEN CAST(CONVERT(codigos_unspsc USING utf8mb4) AS JSON)
    ELSE NULL
END;

ALTER TABLE experiencia_empresa DROP COLUMN codigos_unspsc;
ALTER TABLE experiencia_empresa CHANGE COLUMN codigos_unspsc_new codigos_unspsc JSON NULL;
