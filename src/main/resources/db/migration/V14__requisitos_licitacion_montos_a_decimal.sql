-- requisitos_licitacion fue creada por ddl-auto como DOUBLE. ddl-auto=update NO
-- altera el tipo de columnas existentes, por eso el cambio Double->BigDecimal en la
-- entidad solo surte efecto en BD con este ALTER explicito. MySQL convierte los
-- valores existentes a DECIMAL redondeando a la nueva escala.
--
-- Montos en COP -> DECIMAL(20,2); ratios/indices -> DECIMAL(20,4).
-- Paridad con indicadores_financieros (lado evaluado de la comparacion).
-- poe_anticipo se mantiene como DOUBLE (decision explicita): no es monto COP ni
-- se compara contra indicadores; por eso NO se incluye en este ALTER.
ALTER TABLE requisitos_licitacion
  MODIFY COLUMN presupuesto_licitacion       DECIMAL(20,2),
  MODIFY COLUMN patrimonio_pliego            DECIMAL(20,2),
  MODIFY COLUMN capital_trabajo_req          DECIMAL(20,2),
  MODIFY COLUMN k_residual_proceso           DECIMAL(20,2),
  MODIFY COLUMN liquidez_req                 DECIMAL(20,4),
  MODIFY COLUMN endeudamiento_req            DECIMAL(20,4),
  MODIFY COLUMN cobertura_interes_req        DECIMAL(20,4),
  MODIFY COLUMN rentabilidad_patrimonio_req  DECIMAL(20,4),
  MODIFY COLUMN rentabilidad_activo_req      DECIMAL(20,4);
