package com.elemental.licitapp.SeguimientoProceso.application.ports.in;

import com.elemental.licitapp.SeguimientoProceso.domain.entity.SeguimientoProceso;

/**
 * Puerto publico de inicializacion. Llamado por otros modulos (CuadroDeObra)
 * cuando un proceso transiciona a PRESENTADO. Idempotente: si ya existe
 * seguimiento para el cuadro, retorna el existente sin crear duplicados ni
 * añadir un nuevo evento OFERTA_PRESENTADA.
 */
public interface InicializarSeguimientoUseCase {
    SeguimientoProceso inicializar(Long cuadroDeObraId);
}
