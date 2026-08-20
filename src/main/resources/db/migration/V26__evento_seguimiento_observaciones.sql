-- Observaciones libres de un evento de seguimiento.
-- El frontend ya pedia este campo en el modal de registro y lo pintaba en la linea de
-- tiempo, pero el backend nunca lo acepto: RegistrarEventoRequestDTO solo tenia tipo,
-- fechaEvento y descripcion, asi que lo que el usuario escribia se descartaba en silencio.
-- Es opcional: distingue el "que paso" (descripcion) del "que anotamos al respecto".
ALTER TABLE evento_seguimiento
    ADD COLUMN observaciones TEXT NULL AFTER descripcion;
