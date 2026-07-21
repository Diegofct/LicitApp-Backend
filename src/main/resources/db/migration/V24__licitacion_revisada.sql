-- Marca "Revisado" de la Busqueda SECOP. Antes vivia en el localStorage de cada
-- navegador, asi que no se compartia entre usuarios ni equipos. Pasa a una tabla propia
-- para que todo el equipo vea las mismas licitaciones revisadas.
--
-- Es un conjunto: la presencia de la fila significa "revisada". La llave es el
-- id_del_proceso de SECOP II (identidad unica del proceso). No hay FK contra una tabla
-- de licitaciones porque no existe: SECOP II es una fuente externa de solo lectura.
CREATE TABLE licitacion_revisada (
    id_del_proceso VARCHAR(255) NOT NULL,
    fecha_creacion DATETIME NULL,
    creado_por     BIGINT NULL,
    CONSTRAINT pk_licitacion_revisada PRIMARY KEY (id_del_proceso)
);
