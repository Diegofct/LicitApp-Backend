package com.elemental.licitapp.Sobre2.domain.service;

import com.elemental.licitapp.Sobre2.domain.enums.MetodoPonderacion;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

/**
 * Motor de las formulas de ponderacion de la oferta economica. Dominio puro: sin Spring,
 * sin JPA, sin estado.
 *
 * <p>Fuentes: guia de Colombia Compra Eficiente CCE-EICP-GI-04 (Documentos Tipo de
 * licitacion publica de infraestructura de transporte), numeral 4.1.4 literales A-D, para
 * los metodos vigentes; y el pliego de condiciones tipo de obra publica v2.0 (2015),
 * numeral 4.A, para los metodos del Decreto 1082 de 2015.
 *
 * <p>Reglas transversales que exige la norma y que estan implementadas aqui:
 * <ul>
 *   <li>Los valores entran a las formulas <b>sin decimales</b> (se truncan a pesos enteros).</li>
 *   <li>El puntaje se calcula <b>hasta el septimo decimal</b>.</li>
 *   <li>Los puntajes negativos se llevan a <b>cero</b>.</li>
 *   <li>Solo participan las propuestas validas (no rechazadas); el filtrado ocurre antes.</li>
 * </ul>
 */
public final class CalculadoraPonderacion {

    /** Escala de salida para montos en pesos colombianos. */
    public static final int ESCALA_MONTO = 2;

    /** Escala del puntaje: la norma exige "hasta el septimo (7) decimal". */
    public static final int ESCALA_PUNTAJE = 7;

    /** Escala de trabajo para divisiones intermedias, holgada frente a la de salida. */
    private static final int ESCALA_CALCULO = 12;

    private static final MathContext MC = new MathContext(34, RoundingMode.HALF_UP);
    private static final BigDecimal DOS = BigDecimal.valueOf(2);
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    /** Cero ya escalado. Se escribe literal para que serialice "0.0000000" y no "0E-7". */
    private static final BigDecimal PUNTAJE_CERO = new BigDecimal("0.0000000");

    private CalculadoraPonderacion() {
    }

    // ------------------------------------------------------------------
    // Normalizacion de la muestra
    // ------------------------------------------------------------------

    /**
     * Deja las ofertas como las usa la entidad: sin decimales y ordenadas de manera
     * descendente (el orden que exige el metodo de la mediana). Se trunca, no se redondea:
     * la norma habla del "valor total corregido sin decimales" y truncar es determinista.
     */
    public static List<BigDecimal> normalizar(List<BigDecimal> ofertas) {
        return ofertas.stream()
                .filter(v -> v != null && v.signum() > 0)
                .map(v -> v.setScale(0, RoundingMode.DOWN))
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    // ------------------------------------------------------------------
    // Valor de referencia por metodo
    // ------------------------------------------------------------------

    /**
     * Valor central que arroja el metodo sobre la muestra. Es el numero al que el licitador
     * intenta acercarse ("atinarle a la TRM").
     *
     * @param ofertasDesc muestra ya normalizada (ver {@link #normalizar}), orden descendente
     * @param presupuestoOficial requerido solo por la geometrica del Decreto 1082
     */
    public static BigDecimal valorReferencia(MetodoPonderacion metodo,
                                             List<BigDecimal> ofertasDesc,
                                             BigDecimal presupuestoOficial) {
        if (ofertasDesc.isEmpty()) {
            throw new IllegalArgumentException("No hay ofertas validas para calcular la ponderacion economica.");
        }
        return switch (metodo) {
            case MEDIANA_CON_VALOR_ABSOLUTO -> mediana(ofertasDesc);
            case MEDIA_GEOMETRICA -> mediaGeometrica(ofertasDesc, null, 0);
            case MEDIA_ARITMETICA_BAJA -> promedio(List.of(minimo(ofertasDesc), mediaAritmetica(ofertasDesc)));
            case MENOR_VALOR -> minimo(ofertasDesc);
            case MEDIA_ARITMETICA -> mediaAritmetica(ofertasDesc);
            case MEDIA_ARITMETICA_ALTA -> promedio(List.of(maximo(ofertasDesc), mediaAritmetica(ofertasDesc)));
            case MEDIA_GEOMETRICA_CON_PRESUPUESTO_OFICIAL ->
                    mediaGeometrica(ofertasDesc, exigirPresupuesto(presupuestoOficial), vecesPresupuesto(ofertasDesc.size()));
        };
    }

    /**
     * Valor que hoy obtendria el maximo puntaje. Casi siempre coincide con la oferta mas
     * cercana a la referencia, pero hay dos excepciones que importan:
     * <ul>
     *   <li><b>Mediana con numero par de ofertas</b>: el maximo NO va a la mediana (que es un
     *       promedio y puede no corresponder a ninguna oferta) sino a la propuesta
     *       inmediatamente por debajo de ella.</li>
     *   <li><b>Menor valor</b>: el maximo va a la mas baja, no a la mas cercana a nada.</li>
     * </ul>
     */
    public static BigDecimal valorObjetivo(MetodoPonderacion metodo,
                                           List<BigDecimal> ofertasDesc,
                                           BigDecimal referencia) {
        return switch (metodo) {
            case MENOR_VALOR -> minimo(ofertasDesc);
            case MEDIANA_CON_VALOR_ABSOLUTO -> ofertasDesc.size() % 2 == 0
                    ? inmediatamenteDebajoDeLaMediana(ofertasDesc)
                    : masCercana(ofertasDesc, referencia);
            default -> masCercana(ofertasDesc, referencia);
        };
    }

    /**
     * Denominador de la formula de puntaje. Coincide con la referencia salvo en la mediana
     * con numero par de ofertas, donde la norma sustituye Me por VMe.
     */
    public static BigDecimal baseDePuntaje(MetodoPonderacion metodo,
                                           List<BigDecimal> ofertasDesc,
                                           BigDecimal referencia) {
        if (metodo == MetodoPonderacion.MEDIANA_CON_VALOR_ABSOLUTO && ofertasDesc.size() % 2 == 0) {
            return inmediatamenteDebajoDeLaMediana(ofertasDesc);
        }
        return referencia;
    }

    // ------------------------------------------------------------------
    // Puntaje
    // ------------------------------------------------------------------

    /**
     * Puntaje que obtendria una oferta de valor {@code valor}.
     *
     * <p>Forma general: {@code P * (1 - |base - Vi| / base)}. El menor valor usa otra:
     * {@code P * Vmin / Vi}. Si la oferta es la mas cercana a la base, la norma le asigna
     * directamente el maximo.
     */
    public static BigDecimal puntaje(MetodoPonderacion metodo,
                                     BigDecimal valor,
                                     List<BigDecimal> ofertasDesc,
                                     BigDecimal referencia,
                                     BigDecimal puntajeMaximo) {
        BigDecimal v = valor.setScale(0, RoundingMode.DOWN);
        BigDecimal bruto;
        if (metodo == MetodoPonderacion.MENOR_VALOR) {
            BigDecimal vmin = minimo(ofertasDesc);
            // Una oferta por debajo del minimo de la muestra pasa a ser el nuevo minimo.
            bruto = v.compareTo(vmin) <= 0
                    ? puntajeMaximo
                    : puntajeMaximo.multiply(vmin).divide(v, ESCALA_PUNTAJE, RoundingMode.HALF_UP);
        } else {
            BigDecimal base = baseDePuntaje(metodo, ofertasDesc, referencia);
            BigDecimal objetivo = valorObjetivo(metodo, ofertasDesc, referencia);
            if (v.compareTo(objetivo) == 0) {
                bruto = puntajeMaximo;
            } else {
                BigDecimal desvio = base.subtract(v).abs().divide(base, ESCALA_CALCULO, RoundingMode.HALF_UP);
                bruto = puntajeMaximo.multiply(BigDecimal.ONE.subtract(desvio));
            }
        }
        BigDecimal resultado = bruto.setScale(ESCALA_PUNTAJE, RoundingMode.HALF_UP);
        // "Las propuestas que al aplicar las formulas obtengan puntajes negativos obtienen cero (0) puntos".
        return resultado.signum() < 0 ? PUNTAJE_CERO : resultado;
    }

    // ------------------------------------------------------------------
    // Valor sugerido
    // ------------------------------------------------------------------

    /**
     * Referencia que maximiza el puntaje esperado cuando no se sabe que metodo aplicara,
     * junto con el metodo del que sale.
     *
     * <p>Los metodos del regimen son equiprobables (los sortea la TRM, que se publica
     * despues de ofertar). Maximizar la suma de {@code 1 - |Refk - V| / Refk} equivale a
     * minimizar {@code suma |Refk - V| / Refk}, cuyo optimo es la <b>mediana ponderada</b> de
     * las referencias con pesos {@code 1/Refk}. Eso es exactamente lo que se calcula aqui.
     *
     * <p>Como el optimo es siempre una de las referencias (no un valor interpolado), se puede
     * decir a que tendencia se le esta apuntando: "presentarse en X equivale a apuntarle a la
     * Mediana con Valor Absoluto", que es como se razona la decision.
     *
     * <p>Simplificacion consciente: las referencias se calculan sin incluir la oferta propia
     * (incluirla seria circular, porque desplazaria la referencia que se quiere alcanzar).
     * Para ver el efecto real de la propia oferta esta la simulacion con valor candidato.
     *
     * <p>Desempate: si dos metodos producen la misma referencia gana el primero de la lista,
     * que llega en el orden del regimen (es decir, el de menor rango de TRM).
     *
     * @return null si ninguna referencia es utilizable
     */
    public static ReferenciaDeMetodo referenciaSugerida(List<ReferenciaDeMetodo> referencias) {
        List<ReferenciaDeMetodo> asc = referencias.stream()
                .filter(r -> r != null && r.valor() != null && r.valor().signum() > 0)
                .sorted(Comparator.comparing(ReferenciaDeMetodo::valor))
                .toList();
        if (asc.isEmpty()) {
            return null;
        }
        BigDecimal pesoTotal = BigDecimal.ZERO;
        for (ReferenciaDeMetodo r : asc) {
            pesoTotal = pesoTotal.add(BigDecimal.ONE.divide(r.valor(), ESCALA_CALCULO + 8, RoundingMode.HALF_UP));
        }
        BigDecimal mitad = pesoTotal.divide(DOS, ESCALA_CALCULO + 8, RoundingMode.HALF_UP);
        BigDecimal acumulado = BigDecimal.ZERO;
        for (ReferenciaDeMetodo r : asc) {
            acumulado = acumulado.add(BigDecimal.ONE.divide(r.valor(), ESCALA_CALCULO + 8, RoundingMode.HALF_UP));
            if (acumulado.compareTo(mitad) >= 0) {
                return r.redondeada();
            }
        }
        return asc.get(asc.size() - 1).redondeada();
    }

    /** Referencia calculada por un metodo concreto. */
    public record ReferenciaDeMetodo(MetodoPonderacion metodo, BigDecimal valor) {

        ReferenciaDeMetodo redondeada() {
            return new ReferenciaDeMetodo(metodo, valor.setScale(ESCALA_MONTO, RoundingMode.HALF_UP));
        }
    }

    // ------------------------------------------------------------------
    // Porcentaje
    // ------------------------------------------------------------------

    /**
     * Porcentaje de la oferta frente al presupuesto oficial, expresado x100 (92.94, no
     * 0.9294) y <b>redondeado</b> a dos decimales.
     *
     * <p>Redondeo y no truncado: es lo que hace el ROUND del Excel con el que se viene
     * trabajando, y el porcentaje aqui es un dato de lectura (sobre que tendencia se esta
     * apuntando), no una cifra con la que se liquide nada. Lo que se presenta es el valor en
     * pesos, que no pasa por esta funcion.
     *
     * @return null si no hay presupuesto oficial con el cual comparar
     */
    public static BigDecimal porcentaje(BigDecimal valor, BigDecimal presupuestoOficial) {
        if (valor == null || presupuestoOficial == null || presupuestoOficial.signum() <= 0) {
            return null;
        }
        return valor.multiply(CIEN).divide(presupuestoOficial, ESCALA_MONTO, RoundingMode.HALF_UP);
    }

    // ------------------------------------------------------------------
    // Primitivas estadisticas
    // ------------------------------------------------------------------

    /**
     * Mediana segun los Documentos Tipo: ordenadas de manera descendente, el valor central
     * si el numero de propuestas es impar, o el promedio de los dos centrales si es par.
     */
    public static BigDecimal mediana(List<BigDecimal> ofertasDesc) {
        int n = ofertasDesc.size();
        int medio = n / 2;
        if (n % 2 == 1) {
            return ofertasDesc.get(medio).setScale(ESCALA_MONTO, RoundingMode.HALF_UP);
        }
        return ofertasDesc.get(medio - 1)
                .add(ofertasDesc.get(medio))
                .divide(DOS, ESCALA_MONTO, RoundingMode.HALF_UP);
    }

    /** VMe: propuesta inmediatamente por debajo de la mediana en el orden descendente. */
    private static BigDecimal inmediatamenteDebajoDeLaMediana(List<BigDecimal> ofertasDesc) {
        return ofertasDesc.get(ofertasDesc.size() / 2).setScale(ESCALA_MONTO, RoundingMode.HALF_UP);
    }

    public static BigDecimal mediaAritmetica(List<BigDecimal> ofertas) {
        BigDecimal suma = ofertas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return suma.divide(BigDecimal.valueOf(ofertas.size()), ESCALA_MONTO, RoundingMode.HALF_UP);
    }

    /**
     * Media geometrica. Se calcula con la identidad {@code MG = exp((suma ln Vi) / n)} y no
     * multiplicando: el producto de 38 ofertas de ~1e9 vale ~1e342 y <b>desborda double</b>
     * (maximo 1.8e308), y BigDecimal no tiene raiz n-esima nativa. El error relativo del
     * logaritmo (~1e-15) queda muy por debajo de un centavo sobre montos en pesos.
     *
     * @param presupuestoOficial si no es null, entra al calculo {@code veces} veces
     */
    public static BigDecimal mediaGeometrica(List<BigDecimal> ofertas, BigDecimal presupuestoOficial, int veces) {
        double sumaLogaritmos = 0d;
        int factores = 0;
        for (BigDecimal v : ofertas) {
            sumaLogaritmos += Math.log(v.doubleValue());
            factores++;
        }
        if (presupuestoOficial != null && veces > 0) {
            sumaLogaritmos += veces * Math.log(presupuestoOficial.doubleValue());
            factores += veces;
        }
        double mg = Math.exp(sumaLogaritmos / factores);
        if (!Double.isFinite(mg)) {
            throw new IllegalArgumentException("No se pudo calcular la media geometrica con los valores dados.");
        }
        return new BigDecimal(mg, MC).setScale(ESCALA_MONTO, RoundingMode.HALF_UP);
    }

    /**
     * Numero de veces que el presupuesto oficial entra a la media geometrica del Decreto
     * 1082: una vez por cada tres ofertas validas (1-3 -> 1, 4-6 -> 2, 7-9 -> 3, ...).
     */
    public static int vecesPresupuesto(int numeroDeOfertas) {
        return Math.max(1, (numeroDeOfertas + 2) / 3);
    }

    public static BigDecimal minimo(List<BigDecimal> ofertas) {
        return ofertas.stream().min(Comparator.naturalOrder()).orElseThrow()
                .setScale(ESCALA_MONTO, RoundingMode.HALF_UP);
    }

    public static BigDecimal maximo(List<BigDecimal> ofertas) {
        return ofertas.stream().max(Comparator.naturalOrder()).orElseThrow()
                .setScale(ESCALA_MONTO, RoundingMode.HALF_UP);
    }

    private static BigDecimal promedio(List<BigDecimal> valores) {
        return mediaAritmetica(valores);
    }

    private static BigDecimal masCercana(List<BigDecimal> ofertas, BigDecimal referencia) {
        return ofertas.stream()
                .min(Comparator.comparing(v -> v.subtract(referencia).abs()))
                .orElseThrow()
                .setScale(ESCALA_MONTO, RoundingMode.HALF_UP);
    }

    private static BigDecimal exigirPresupuesto(BigDecimal presupuestoOficial) {
        if (presupuestoOficial == null || presupuestoOficial.signum() <= 0) {
            throw new IllegalArgumentException(
                    "La media geometrica con presupuesto oficial requiere el presupuesto oficial del proceso.");
        }
        return presupuestoOficial;
    }
}
