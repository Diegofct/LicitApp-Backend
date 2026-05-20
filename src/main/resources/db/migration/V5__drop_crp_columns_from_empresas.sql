-- Elimina columnas residuales de Capacidad Residual del Proponente que quedaron
-- en la tabla empresas tras la extraccion a la entidad CapacidadResidualProponente
-- (tabla capacidad_residual_proponente). spring.jpa.hibernate.ddl-auto=update
-- no elimina columnas obsoletas, por eso quedaron huerfanas tras el refactor.
ALTER TABLE empresas
  DROP COLUMN capacidad_organizacion,
  DROP COLUMN puntaje_experiencia,
  DROP COLUMN puntaje_tecnico,
  DROP COLUMN puntaje_financiero,
  DROP COLUMN saldos_contratos_ejecucion;
