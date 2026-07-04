package com.elemental.licitapp.InteligenciaArtificial.domain.entity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/**
 * Datos crudos extraidos de un RUP por el modelo. Es el tipo que devuelve el adaptador
 * del proveedor LLM; el {@code ExtraccionDocumentosAppService} lo valida y normaliza
 * antes de envolverlo en un {@link ResultadoExtraccion}.
 */
public record DatosRupExtraidos(

        @JsonPropertyDescription("Informacion general del proponente")
        InformacionGeneralRup informacionGeneral,

        @JsonPropertyDescription("Todos los cierres fiscales reportados en el RUP")
        List<CierreFiscalExtraido> cierresFiscales,

        @JsonPropertyDescription("Todos los contratos de experiencia inscritos en el RUP (lista vacia si no hay)")
        List<ExperienciaExtraida> experiencias
) {
}
