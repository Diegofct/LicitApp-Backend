-- Nombre del proponente plural (ej. "Consorcio Vías del Norte").
-- Nullable a nivel de esquema porque INDIVIDUAL no requiere nombre; la obligatoriedad
-- para CONSORCIO/UNION_TEMPORAL se valida en ConformacionConsorcioAppService.

ALTER TABLE conformacion_consorcio
    ADD COLUMN nombre VARCHAR(255) NULL AFTER cuadro_de_obra_id;
