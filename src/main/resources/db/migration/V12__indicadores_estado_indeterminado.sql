-- Estado de cada ratio financiero del RUP.
-- Antes, una división por cero se persistía como 0, lo que el análisis de cumplimiento
-- interpretaba como un valor real (falsos CUMPLE en endeudamiento, falsos NO CUMPLE en el resto).
-- Ahora un indicador indeterminado guarda valor NULL y su estado explica el porqué.

ALTER TABLE indicadores_financieros
    ADD COLUMN estado_liquidez                VARCHAR(30) NULL,
    ADD COLUMN estado_endeudamiento           VARCHAR(30) NULL,
    ADD COLUMN estado_cobertura_interes       VARCHAR(30) NULL,
    ADD COLUMN estado_rentabilidad_patrimonio VARCHAR(30) NULL,
    ADD COLUMN estado_rentabilidad_activo     VARCHAR(30) NULL;

-- Backfill de registros existentes: los valores ya calculados (incluidos los 0 antiguos,
-- que no se pueden distinguir a posteriori de un indeterminado) se marcan como CALCULABLE.
-- Al volver a guardar/recalcular la empresa, recalcular() asigna el estado correcto.
UPDATE indicadores_financieros
SET estado_liquidez                = 'CALCULABLE',
    estado_endeudamiento           = 'CALCULABLE',
    estado_cobertura_interes       = 'CALCULABLE',
    estado_rentabilidad_patrimonio = 'CALCULABLE',
    estado_rentabilidad_activo     = 'CALCULABLE';
