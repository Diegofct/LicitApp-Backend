-- Limpieza de residuos de la era ddl-auto (ambos verificados sin datos):
--
-- 1) analisis_cumplimiento: tabla huerfana (0 filas, sin FKs) de una entidad que ya no
--    existe. El resultado de la evaluacion hoy se calcula al vuelo (ResultadoEvaluacion
--    es un record en memoria, no se persiste), asi que la tabla nunca vuelve a usarse.
--
-- 2) requisitos_licitacion.n_meses: V18 ya la elimino, pero en ese mismo arranque la
--    entidad aun mapeaba nMeses y ddl-auto=update la recreo acto seguido. Como ddl-auto
--    nunca borra columnas, quedo huerfana. Hoy ninguna entidad la mapea, por lo que este
--    DROP si es definitivo.

DROP TABLE IF EXISTS analisis_cumplimiento;

ALTER TABLE requisitos_licitacion
  DROP COLUMN n_meses;
