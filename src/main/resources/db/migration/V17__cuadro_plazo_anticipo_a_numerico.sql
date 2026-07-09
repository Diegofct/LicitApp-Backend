-- RF4: plazo y anticipo de cuadro_de_obra pasan de texto libre ("6 meses", "30%")
-- a numerico (INT). ddl-auto=update NO altera el tipo de columnas existentes, por eso
-- el cambio String->Integer en la entidad solo surte efecto en BD con este script.
--
-- Antes de cambiar el tipo se limpian los valores actuales dejando solo los digitos.
-- Los vacios se convierten a NULL para que el CAST implicito del MODIFY no falle.
-- Nota: se asume que plazo (meses) y anticipo (%) son enteros; si un valor traia
-- decimales ("2.5%"), el punto se descarta (queda 25). No aplica a los datos actuales.
UPDATE cuadro_de_obra
   SET plazo = NULLIF(REGEXP_REPLACE(COALESCE(plazo, ''), '[^0-9]', ''), '');

UPDATE cuadro_de_obra
   SET anticipo = NULLIF(REGEXP_REPLACE(COALESCE(anticipo, ''), '[^0-9]', ''), '');

ALTER TABLE cuadro_de_obra
  MODIFY COLUMN plazo    INT,
  MODIFY COLUMN anticipo INT;
