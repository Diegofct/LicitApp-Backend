package com.elemental.licitapp.InteligenciaArtificial.application.ports.out;

import com.elemental.licitapp.InteligenciaArtificial.domain.entity.DatosRupExtraidos;

/**
 * Frontera con el proveedor LLM. El adaptador que lo implementa
 * ({@code AnthropicExtractorAdapter}) es el unico punto del codigo que conoce el SDK
 * del proveedor; el resto del slice trabaja contra esta interfaz.
 */
public interface ExtractorDocumentosPort {

    /**
     * Envia el PDF al modelo y devuelve los datos crudos extraidos (sin validar/normalizar).
     *
     * @throws com.elemental.licitapp.Exception.ProcesamientoPliegoException si el proveedor falla o hay timeout
     */
    DatosRupExtraidos extraerDatosRup(byte[] pdf);
}
