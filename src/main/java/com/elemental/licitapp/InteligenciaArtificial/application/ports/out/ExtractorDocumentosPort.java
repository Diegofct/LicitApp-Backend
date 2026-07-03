package com.elemental.licitapp.InteligenciaArtificial.application.ports.out;

import com.elemental.licitapp.InteligenciaArtificial.domain.entity.DatosRupExtraidos;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.RequisitosPliegoExtraidos;

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

    /**
     * Envia el PDF de un pliego al modelo y devuelve los requisitos habilitantes crudos
     * (sin validar/normalizar).
     *
     * @throws com.elemental.licitapp.Exception.ProcesamientoPliegoException si el proveedor falla o hay timeout
     */
    RequisitosPliegoExtraidos extraerRequisitosPliego(byte[] pdf);
}
