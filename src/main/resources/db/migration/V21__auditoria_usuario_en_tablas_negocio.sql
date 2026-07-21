-- Auditoria de autoria: conecta `usuarios` (hasta ahora una isla sin ninguna relacion)
-- con las tablas de negocio donde interesa saber quien hizo que.
--
-- Alcance decidido: cuadro_de_obra, conformacion_consorcio y seguimiento_proceso.
-- `empresas` queda fuera a proposito (datos maestros, se auditara mas adelante si hace falta).
--
-- Forma: columnas de autoria sobre la tabla de negocio (no bitacora). Responde
-- "quien lo creo / quien lo toco de ultimo", no el historial de cambios.
--
-- Las FK NO llevan ON DELETE: si un usuario tiene rastro de auditoria, la BD impide
-- borrarlo y el rastro nunca queda huerfano. Para dar de baja a alguien se usa
-- usuarios.activo = 0, que es justamente para lo que existe ese flag.
--
-- Todas las columnas son NULL: las filas ya existentes no tienen autor conocido y
-- no hay forma honesta de inventarlo.

-- cuadro_de_obra: no tenia ninguna fecha tecnica, se agrega el cuarteto completo.
ALTER TABLE cuadro_de_obra
  ADD COLUMN fecha_creacion DATETIME(6) NULL,
  ADD COLUMN fecha_actualizacion DATETIME(6) NULL,
  ADD COLUMN creado_por BIGINT NULL,
  ADD COLUMN actualizado_por BIGINT NULL,
  ADD CONSTRAINT fk_cuadro_de_obra_creado_por
      FOREIGN KEY (creado_por) REFERENCES usuarios (id),
  ADD CONSTRAINT fk_cuadro_de_obra_actualizado_por
      FOREIGN KEY (actualizado_por) REFERENCES usuarios (id);

-- conformacion_consorcio: fecha_conformacion ya actua como fecha de creacion
-- (la setea el AppService al conformar), asi que no se duplica.
ALTER TABLE conformacion_consorcio
  ADD COLUMN fecha_actualizacion DATETIME(6) NULL,
  ADD COLUMN creado_por BIGINT NULL,
  ADD COLUMN actualizado_por BIGINT NULL,
  ADD CONSTRAINT fk_conformacion_consorcio_creado_por
      FOREIGN KEY (creado_por) REFERENCES usuarios (id),
  ADD CONSTRAINT fk_conformacion_consorcio_actualizado_por
      FOREIGN KEY (actualizado_por) REFERENCES usuarios (id);

-- seguimiento_proceso: fecha_inicio ya actua como fecha de creacion.
ALTER TABLE seguimiento_proceso
  ADD COLUMN fecha_actualizacion DATETIME(6) NULL,
  ADD COLUMN creado_por BIGINT NULL,
  ADD COLUMN actualizado_por BIGINT NULL,
  ADD CONSTRAINT fk_seguimiento_proceso_creado_por
      FOREIGN KEY (creado_por) REFERENCES usuarios (id),
  ADD CONSTRAINT fk_seguimiento_proceso_actualizado_por
      FOREIGN KEY (actualizado_por) REFERENCES usuarios (id);
