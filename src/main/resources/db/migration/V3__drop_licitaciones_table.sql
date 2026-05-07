-- El módulo Licitaciones es proxy puro a la API de SECOP II y no persiste registros.
-- La tabla `licitaciones` quedó huérfana cuando la entidad de dominio se desacopló de JPA.
DROP TABLE IF EXISTS licitaciones;
