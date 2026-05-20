-- Elimina la columna ct_proceso residual de la tabla requisitos_licitacion.
-- Esta columna fue reemplazada por capital_trabajo_req durante el refactor del
-- modulo CuadroDeObra. spring.jpa.hibernate.ddl-auto=update no elimina columnas
-- obsoletas, por eso quedo huerfana.
ALTER TABLE requisitos_licitacion
  DROP COLUMN ct_proceso;
