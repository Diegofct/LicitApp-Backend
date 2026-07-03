-- Marca temporal del cálculo de la K residual del proponente. Permite seleccionar de forma
-- determinista la CRP vigente (la más reciente) en lugar de "el último registro de la lista".
-- Nullable: los registros existentes previos a esta migración no la tienen; la capa de
-- servicio (EmpresaService) la setea al recalcular. Tipo DATETIME(6) por consistencia con
-- las demás columnas de fecha del proyecto (fecha_conformacion, fecha_creacion).
ALTER TABLE capacidad_residual_proponente
  ADD COLUMN fecha_calculo DATETIME(6) NULL;
