package com.elemental.licitapp.Sobre2.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Una oferta tal como la publica SECOP II, ya deduplicada y con el texto reparado.
 * Es el contrato de salida del puerto {@code OfertasSecopPort}: el slice no conoce el
 * formato crudo del dataset.
 *
 * @param identificadorOferta clave de deduplicacion (formato {@code CO1.RPL.xxxxxxx}); una
 *                            misma oferta llega repetida, una fila por integrante del
 *                            consorcio o union temporal
 * @param nitOferente         null en consorcios/UT: SECOP publica ahi "No Definido" o
 *                            "000000000", asi que el oferente se identifica por nombre
 */
public record OfertaImportada(
        String identificadorOferta,
        String referenciaOferta,
        String nombreOferente,
        String nitOferente,
        BigDecimal valorOferta,
        String moneda,
        LocalDate fechaRegistro
) {
}
