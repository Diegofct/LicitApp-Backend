-- El numero de proceso (num_proceso, ej. "LP-005-2026") NO identifica un proceso: en
-- SECOP II solo es unico dentro de una misma entidad, asi que dos entidades distintas
-- pueden publicar cada una su "LP-005-2026". Cruzar por num_proceso hacia que el
-- frontend resaltara como "ya agregadas" licitaciones que no lo estaban y abriera el
-- detalle de un proceso ajeno.
--
-- Se agrega id_del_proceso (identificador unico de SECOP II) como identidad del cruce.
-- num_proceso se conserva: sigue siendo el dato que lee y reporta el usuario.

ALTER TABLE cuadro_de_obra
    ADD COLUMN id_del_proceso VARCHAR(255) NULL AFTER num_proceso;

-- Unico cuando esta presente. En MySQL un indice UNIQUE admite multiples NULL, que es
-- justo lo que se necesita: los procesos cargados a mano no existen en SECOP y quedan
-- con id_del_proceso NULL, asi que pueden coexistir sin colisionar entre si.
--
-- Las filas existentes quedan todas en NULL, por lo que este indice no puede fallar al
-- aplicarse. Mientras no se les asigne el identificador, esos cuadros historicos no
-- resaltaran su fila en la Busqueda SECOP (ver "Backfill" en la spec).
ALTER TABLE cuadro_de_obra
    ADD CONSTRAINT uq_cuadro_de_obra_id_del_proceso UNIQUE (id_del_proceso);
