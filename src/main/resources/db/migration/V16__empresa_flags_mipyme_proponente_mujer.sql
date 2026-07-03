-- Separa en dos flags booleanos explícitos los conceptos que antes se infería con un
-- heurístico de subcadenas sobre tamano_empresa: MiPyme (tamaño) y proponente mujer
-- (condición). tamano_empresa se conserva como dato descriptivo.
--
-- Tipo BIT(1) NOT NULL DEFAULT b'0' por consistencia con el patrón de booleanos del proyecto
-- (usuarios.activo). Default false: sin el flag, no se otorga el beneficio de +2 contratos de
-- experiencia (opción conservadora).
ALTER TABLE empresas
  ADD COLUMN mipyme            BIT(1) NOT NULL DEFAULT b'0',
  ADD COLUMN proponente_mujer  BIT(1) NOT NULL DEFAULT b'0';

-- Backfill: reproduce el heurístico anterior sobre tamano_empresa para no cambiar el
-- comportamiento de los registros existentes (si no, perderían el beneficio +2 hasta
-- re-guardarse). Se usan substrings ASCII-safe (%peque%, %median%, %pyme%) para evitar
-- problemas de acentos/ñ y de colación. LIKE es case-insensitive en las colaciones por
-- defecto de MySQL; se aplica LOWER() por robustez.
UPDATE empresas
SET mipyme = b'1'
WHERE tamano_empresa IS NOT NULL
  AND (LOWER(tamano_empresa) LIKE '%micro%'
    OR LOWER(tamano_empresa) LIKE '%peque%'
    OR LOWER(tamano_empresa) LIKE '%median%'
    OR LOWER(tamano_empresa) LIKE '%pyme%');

UPDATE empresas
SET proponente_mujer = b'1'
WHERE tamano_empresa IS NOT NULL
  AND LOWER(tamano_empresa) LIKE '%mujer%';
