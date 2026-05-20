-- La relacion CuadroDeObra <-> RequisitoLicitacion es 1:1 a nivel de dominio,
-- pero la tabla no tenia constraint UNIQUE en cuadro_de_obra_id, por lo que
-- multiples POST al mismo cuadro pudieron crear varios requisitos.

-- 1) Limpieza preventiva de duplicados: conserva el registro de mayor id
--    (el mas reciente) para cada cuadro_de_obra_id.
DELETE r1 FROM requisitos_licitacion r1
INNER JOIN requisitos_licitacion r2
  ON r1.cuadro_de_obra_id = r2.cuadro_de_obra_id
  AND r1.id < r2.id;

-- 2) Refuerzo de la invariante 1:1 a nivel de BD.
ALTER TABLE requisitos_licitacion
  ADD CONSTRAINT uq_requisitos_licitacion_cuadro UNIQUE (cuadro_de_obra_id);
