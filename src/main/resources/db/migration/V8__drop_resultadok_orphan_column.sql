-- Elimina la columna huerfana 'resultadok' de la tabla capacidad_residual_proponente.
-- La columna existio cuando la propiedad resultadoK en la entidad
-- CapacidadResidualProponente no tenia @Column explicito y la naming strategy
-- por defecto la materializo como 'resultadok'. Al agregar @Column(name = "resultado_k")
-- quedo huerfana recibiendo NULL en cada insert.
-- spring.jpa.hibernate.ddl-auto=update no elimina columnas obsoletas.
ALTER TABLE capacidad_residual_proponente
  DROP COLUMN resultadok;
