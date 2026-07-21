-- Se elimina el rol PROPIETARIO del modelo de dominio (el enum Rol ya solo tiene
-- ANALISTA y ADMIN). Esta migracion reasigna a ANALISTA cualquier usuario que aun
-- tuviera PROPIETARIO, para no dejar filas con un rol inexistente en el codigo.
-- Es idempotente en la practica: si no hay ningun PROPIETARIO, no afecta filas.

UPDATE usuarios
SET rol = 'ANALISTA',
    fecha_actualizacion = NOW()
WHERE rol = 'PROPIETARIO';
