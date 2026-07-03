package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

/**
 * Tipo de requisito habilitante evaluado. Es la CLAVE DE IDENTIDAD de un
 * {@link DetalleRequisito} en toda la lógica de análisis y sugerencia de consorcio
 * (déficit, cobertura, degradación); antes se usaba el String del nombre, frágil ante
 * cambios de literal.
 *
 * <p>La etiqueta es la única fuente del texto legible que ve el frontend: se expone
 * vía {@link DetalleRequisito#nombre()} y se mantiene aquí para no duplicar literales.</p>
 */
public enum TipoRequisito {
    GENERAL("General"),
    LIQUIDEZ("Índice de Liquidez"),
    ENDEUDAMIENTO("Nivel de Endeudamiento"),
    CAPITAL_TRABAJO("Capital de Trabajo"),
    PATRIMONIO("Patrimonio Total"),
    RCI("Cobertura de Intereses"),
    ROE("ROE"),
    ROA("ROA"),
    EXPERIENCIA("Experiencia Acumulada (SMMLV)"),
    CAPACIDAD_RESIDUAL("Capacidad Residual (K)");

    private final String etiqueta;

    TipoRequisito(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }
}
