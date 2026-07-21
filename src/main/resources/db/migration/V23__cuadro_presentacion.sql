-- Marca "nos presentamos" del Cuadro de Obra (sin marca / SI / NO). Antes vivia en el
-- localStorage de cada navegador, asi que no se compartia entre usuarios ni equipos.
-- Pasa a ser un atributo del cuadro para que todo el equipo vea la misma marca.
-- Tri-estado: NULL = sin marca; 'SI'/'NO' = decision registrada.
ALTER TABLE cuadro_de_obra
    ADD COLUMN presentacion VARCHAR(3) NULL AFTER estado;
