package com.elemental.licitapp.InteligenciaArtificial.domain.entity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Requisitos habilitantes crudos extraidos de un pliego de condiciones por el modelo.
 * Es el tipo que devuelve el adaptador del proveedor LLM; el
 * {@code ExtraccionPliegoAppService} lo valida y normaliza antes de envolverlo en un
 * {@link ResultadoExtraccionPliego}.
 *
 * <p>Mapea 1:1 a la entidad {@code RequisitoLicitacion} de CuadroDeObra. Los indicadores
 * financieros se piden ya en <b>decimal</b> (ej. "endeudamiento &le; 70%" &rarr; 0.70,
 * "liquidez &ge; 1,2" &rarr; 1.2) para casar con las unidades de la empresa.
 */
public record RequisitosPliegoExtraidos(

        @JsonPropertyDescription("Experiencia general exigida (texto literal del requisito)")
        String general,

        @JsonPropertyDescription("Experiencia especifica 1 (texto literal)")
        String especifica1,

        @JsonPropertyDescription("Experiencia especifica 2 (texto literal)")
        String especifica2,

        @JsonPropertyDescription("Experiencia secundaria o adicional (texto literal)")
        String secundaria,

        @JsonPropertyDescription("Numero maximo de contratos para acreditar experiencia")
        Integer contrato,

        @JsonPropertyDescription("Plazo del proyecto en meses (n)")
        Integer n,

        @JsonPropertyDescription("Presupuesto oficial del proceso (valor absoluto, sin separadores de miles)")
        Double presupuesto,

        @JsonPropertyDescription("Patrimonio minimo exigido (valor absoluto)")
        Double patrimonio,

        @JsonPropertyDescription("Capital de trabajo minimo exigido (valor absoluto)")
        Double capitalTrabajo,

        @JsonPropertyDescription("Indice de liquidez minimo, en decimal (ej. 1.2)")
        Double liquidez,

        @JsonPropertyDescription("Nivel de endeudamiento maximo, en decimal 0-1 (ej. 70% -> 0.70)")
        Double endeudamiento,

        @JsonPropertyDescription("Razon de cobertura de intereses minima, en decimal (ej. 3.0)")
        Double razonCoberturaInteres,

        @JsonPropertyDescription("Rentabilidad del patrimonio minima, en decimal 0-1 (ej. 10% -> 0.10)")
        Double rentabilidadPatrimonio,

        @JsonPropertyDescription("Rentabilidad del activo minima, en decimal 0-1 (ej. 5% -> 0.05)")
        Double rentabilidadActivo,

        @JsonPropertyDescription("Capacidad residual (K) exigida por el proceso")
        Double kResidualProceso,

        @JsonPropertyDescription("Porcentaje de anticipo segun el POE, en decimal 0-1 (ej. 30% -> 0.30)")
        Double poeAnticipo
) {
}
