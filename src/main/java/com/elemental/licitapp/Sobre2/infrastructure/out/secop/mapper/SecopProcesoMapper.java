package com.elemental.licitapp.Sobre2.infrastructure.out.secop.mapper;

import com.elemental.licitapp.Sobre2.domain.entity.ProcesoSecop;
import com.elemental.licitapp.Sobre2.infrastructure.out.secop.dto.SecopProcesoDTO;

/**
 * Traduce una fila del dataset de procesos al modelo del slice. Clase utilitaria con metodos
 * estaticos, como el resto de mappers del proyecto.
 *
 * <p>A diferencia de {@link SecopOfertaMapper} <b>no</b> repara la doble codificacion UTF-8:
 * de este dataset solo se leen identificadores, NIT y monto, campos que no llevan tildes. El
 * texto de p6dx que si viene con mojibake (entidad, descripcion) no se pide aqui.
 */
public final class SecopProcesoMapper {

    private SecopProcesoMapper() {
    }

    public static ProcesoSecop toDomain(SecopProcesoDTO dto) {
        if (dto == null) {
            return null;
        }
        return new ProcesoSecop(
                limpiar(dto.getIdDelProceso()),
                limpiar(dto.getIdDelPortafolio()),
                soloDigitos(dto.getNitEntidad()),
                limpiar(dto.getReferenciaDelProceso()),
                dto.getPrecioBase());
    }

    /** El conteo viaja como texto y puede venir vacio o no numerico. */
    public static Integer aEntero(String valor) {
        String limpio = limpiar(valor);
        if (limpio == null) {
            return null;
        }
        try {
            return Integer.valueOf(limpio);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * El NIT llega numerico en p6dx, pero el que teclea el analista puede venir con puntos o
     * con digito de verificacion. Se normalizan los dos lados a solo digitos para poder
     * compararlos.
     */
    static String soloDigitos(String nit) {
        String limpio = limpiar(nit);
        if (limpio == null) {
            return null;
        }
        String digitos = limpio.replaceAll("\\D", "");
        return digitos.isEmpty() ? null : digitos;
    }

    private static String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String trim = valor.trim();
        return trim.isEmpty() ? null : trim;
    }
}
