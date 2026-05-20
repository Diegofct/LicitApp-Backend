package com.elemental.licitapp.CuadroDeObra.application.ports.out;

/**
 * Puerto de salida hacia el modulo SeguimientoProceso. Disparado cuando un
 * cuadro de obra transiciona a PRESENTADO, para inicializar el seguimiento
 * y registrar automaticamente el evento OFERTA_PRESENTADA.
 *
 * La implementacion debe ser idempotente.
 */
public interface InicializarSeguimientoPort {
    void inicializar(Long cuadroDeObraId);
}
