-- RF5: el plazo (meses) digitado en el Cuadro de Obra pasa a ser un requisito de la
-- licitacion; se agrega la columna plazo_meses a requisitos_licitacion.
-- RF7: se elimina el indicador N (columna n_meses), que ya no se usa en el formulario.
ALTER TABLE requisitos_licitacion
  ADD COLUMN plazo_meses INT NULL AFTER num_contrato;

ALTER TABLE requisitos_licitacion
  DROP COLUMN n_meses;
