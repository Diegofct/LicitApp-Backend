package com.elemental.licitapp.Sobre2.infrastructure.out.secop.mapper;

import com.elemental.licitapp.Sobre2.domain.entity.OfertaImportada;
import com.elemental.licitapp.Sobre2.infrastructure.out.secop.dto.SecopOfertaDTO;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Traduce una fila cruda del dataset de ofertas al modelo del slice. Clase utilitaria con
 * metodos estaticos, como el resto de mappers del proyecto.
 */
public final class SecopOfertaMapper {

    /**
     * Valores con los que SECOP II rellena el NIT cuando el proponente es un consorcio o
     * una union temporal, que es el caso mayoritario en obra publica.
     */
    private static final Set<String> NITS_NO_UTILIZABLES = Set.of("no definido", "000000000", "0", "n/a", "na");

    private SecopOfertaMapper() {
    }

    public static OfertaImportada toDomain(SecopOfertaDTO dto) {
        if (dto == null) {
            return null;
        }
        return new OfertaImportada(
                limpiar(dto.getIdentificadorDeLaOferta()),
                repararCodificacion(limpiar(dto.getReferenciaDeLaOferta())),
                repararCodificacion(limpiar(dto.getNombreProveedor())),
                normalizarNit(dto.getNitDelProveedor()),
                dto.getValorDeLaOferta(),
                repararCodificacion(limpiar(dto.getMoneda())),
                aFecha(dto.getFechaDeRegistro()));
    }

    /**
     * Este dataset devuelve el texto en <b>UTF-8 doble codificado</b>: "Licitacion publica"
     * llega como "LicitaciÃ³n pÃºblica". Se revierte re-leyendo los bytes ISO-8859-1 como
     * UTF-8.
     *
     * <p>Dos guardas para no danar texto que si estaba bien:
     * <ol>
     *   <li>Si algun caracter esta por encima de U+00FF el texto no puede estar doble
     *       codificado, y re-codificarlo lo destruiria (los caracteres fuera de ISO-8859-1
     *       se convierten en '?').</li>
     *   <li>Si tras re-decodificar aparece el caracter de reemplazo U+FFFD, la secuencia de
     *       bytes no era UTF-8 valido: se devuelve el original.</li>
     * </ol>
     *
     * <p>El dataset p6dx-8zbt que usa el slice Licitaciones no tiene este problema; su
     * mapper no debe aplicar esta reparacion.
     */
    static String repararCodificacion(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) > 0xFF) {
                return texto;
            }
        }
        String reparado = new String(texto.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return reparado.indexOf('\uFFFD') >= 0 ? texto : reparado;
    }

    /**
     * El tipo {@code calendar_date} de Socrata viaja como "2025-03-14T00:00:00.000", que el
     * deserializador de LocalDate rechaza. Se corta la parte de fecha y se ignora la hora,
     * que en este dataset siempre es medianoche.
     */
    static java.time.LocalDate aFecha(String valor) {
        String limpio = limpiar(valor);
        if (limpio == null || limpio.length() < 10) {
            return null;
        }
        try {
            return java.time.LocalDate.parse(limpio.substring(0, 10));
        } catch (java.time.format.DateTimeParseException ex) {
            return null;
        }
    }

    /**
     * @return null cuando el NIT no sirve para identificar al proponente (consorcios/UT),
     *         para que el resto del sistema no intente casarlo contra Empresa
     */
    static String normalizarNit(String nit) {
        String limpio = limpiar(nit);
        if (limpio == null || NITS_NO_UTILIZABLES.contains(limpio.toLowerCase())) {
            return null;
        }
        return limpio;
    }

    private static String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String trim = valor.trim();
        return trim.isEmpty() ? null : trim;
    }
}
