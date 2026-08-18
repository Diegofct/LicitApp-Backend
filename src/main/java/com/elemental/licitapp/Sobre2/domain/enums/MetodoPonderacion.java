package com.elemental.licitapp.Sobre2.domain.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Metodos de ponderacion de la oferta economica.
 *
 * <p>La entidad no escoge el metodo: lo sortea la TRM. Toma los centavos de la Tasa de
 * Cambio Representativa del Mercado del dia habil siguiente a la apertura efectiva del
 * Sobre 2 y los ubica en el rango correspondiente. Como esa TRM se publica DESPUES de que
 * el proponente oferto, los cuatro metodos del regimen son equiprobables al momento de
 * decidir el valor: por eso el analisis calcula todos y no uno.
 *
 * <p>Ojo con el nombre del primero: en el regimen vigente es una <b>mediana</b> estadistica,
 * no una media. Los Documentos Tipo lo definen ordenando las propuestas de manera
 * descendente y tomando el valor central (impar) o el promedio de los dos centrales (par).
 */
public enum MetodoPonderacion {

    /** Me = mediana(V1..Vn). Documentos Tipo 4.1.4.A. */
    MEDIANA_CON_VALOR_ABSOLUTO("Mediana con valor absoluto", RegimenPonderacion.DOCUMENTOS_TIPO, "0.00 - 0.24"),

    /** MG = (V1*...*Vn)^(1/n). Sin presupuesto oficial. Documentos Tipo 4.1.4.B. */
    MEDIA_GEOMETRICA("Media geometrica", RegimenPonderacion.DOCUMENTOS_TIPO, "0.25 - 0.49"),

    /** XB = (Vmin + X)/2, con X = promedio simple de TODAS las validas. Documentos Tipo 4.1.4.C. */
    MEDIA_ARITMETICA_BAJA("Media aritmetica baja", RegimenPonderacion.DOCUMENTOS_TIPO, "0.50 - 0.74"),

    /** Vmin = minimo(V1..Vn). Presente en los dos regimenes. */
    MENOR_VALOR("Menor valor", RegimenPonderacion.AMBOS, "0.75 - 0.99"),

    /** X = (suma Vi)/n. Decreto 1082, art. 2.2.1.1.2.2.2. */
    MEDIA_ARITMETICA("Media aritmetica", RegimenPonderacion.DECRETO_1082, "0.00 - 0.24"),

    /** XA = (Vmax + X)/2. Decreto 1082. Eliminada de los Documentos Tipo. */
    MEDIA_ARITMETICA_ALTA("Media aritmetica alta", RegimenPonderacion.DECRETO_1082, "0.25 - 0.49"),

    /** MGpo = (PO^nv * V1*...*Vn)^(1/(nv+n)). Decreto 1082. Requiere presupuesto oficial. */
    MEDIA_GEOMETRICA_CON_PRESUPUESTO_OFICIAL(
            "Media geometrica con presupuesto oficial", RegimenPonderacion.DECRETO_1082, "0.50 - 0.74");

    private final String nombre;
    private final RegimenPonderacion regimen;
    private final String rangoTrm;

    MetodoPonderacion(String nombre, RegimenPonderacion regimen, String rangoTrm) {
        this.nombre = nombre;
        this.regimen = regimen;
        this.rangoTrm = rangoTrm;
    }

    public String getNombre() {
        return nombre;
    }

    public RegimenPonderacion getRegimen() {
        return regimen;
    }

    /** Centavos de la TRM que hacen que la entidad aplique este metodo. */
    public String getRangoTrm() {
        return rangoTrm;
    }

    /** Solo la geometrica del Decreto 1082 necesita el presupuesto oficial como insumo. */
    public boolean requierePresupuestoOficial() {
        return this == MEDIA_GEOMETRICA_CON_PRESUPUESTO_OFICIAL;
    }

    /**
     * Metodos aplicables a un regimen, en el orden de los rangos de TRM. MENOR_VALOR sale
     * en ambos porque los dos regimenes lo incluyen con el mismo rango (0.75 - 0.99).
     */
    public static List<MetodoPonderacion> delRegimen(RegimenPonderacion regimen) {
        return Arrays.stream(values())
                .filter(m -> m.regimen == regimen || m.regimen == RegimenPonderacion.AMBOS)
                .sorted((a, b) -> a.rangoTrm.compareTo(b.rangoTrm))
                .toList();
    }
}
