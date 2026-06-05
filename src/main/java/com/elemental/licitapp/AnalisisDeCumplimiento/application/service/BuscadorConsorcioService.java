package com.elemental.licitapp.AnalisisDeCumplimiento.application.service;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.DetalleRequisito;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.IntegranteEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.IntegrantePropuesto;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.PropuestaConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.RepartidorParticipacion;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Construye propuestas de consorcio cuando una empresa no cumple los requisitos del pliego
 * por sí sola.
 *
 * <p>Estrategia: en vez de recombinar todo al 50/50 y exigir cumplimiento global (lo que
 * degradaba los requisitos que la empresa ya cumplía), parte del <b>déficit</b> de la
 * solicitante y agrega candidatas mediante un algoritmo <b>greedy por cobertura</b>, sin
 * permitir que ninguna incorporación degrade un requisito ya cumplido.</p>
 *
 * <ul>
 *   <li><b>Reparto proporcional al aporte:</b> el complemento (1 − %solicitante) se reparte
 *       entre las candidatas según su fuerza relativa en los rubros deficitarios.</li>
 *   <li><b>Varias alternativas:</b> se corre el greedy con cada candidata como socio de
 *       arranque, se deduplican los conjuntos y se devuelven las mejores (top-K).</li>
 * </ul>
 */
@Service
public class BuscadorConsorcioService {

    /** Tope de integrantes del consorcio sugerido (solicitante + hasta 3 candidatas). */
    private static final int MAX_INTEGRANTES = 4;
    private static final int ESCALA = 4;

    private final AnalizadorCumplimientoService analizador;

    public BuscadorConsorcioService(AnalizadorCumplimientoService analizador) {
        this.analizador = analizador;
    }

    public List<PropuestaConsorcio> buscar(Empresa solicitante,
                                           List<Empresa> candidatas,
                                           RequisitoLicitacion requisito,
                                           BigDecimal porcentajeSolicitante,
                                           int maxPropuestas) {

        ResultadoEvaluacion individual = analizador.analizar(solicitante, requisito, TipoParticipacion.INDIVIDUAL);
        List<String> deficit = nombresNoCumplidos(individual);
        if (deficit.isEmpty()) {
            return List.of();
        }

        List<Empresa> pool = candidatas.stream()
                .filter(c -> c.getId() != null && !c.getId().equals(solicitante.getId()))
                .filter(c -> c.getIndicadores() != null)
                .toList();
        if (pool.isEmpty()) {
            return List.of();
        }

        Contexto ctx = construirContexto(solicitante, pool, requisito, porcentajeSolicitante, deficit);

        List<PropuestaConsorcio> propuestas = new ArrayList<>();
        Set<Set<Long>> conjuntosVistos = new HashSet<>();
        for (Empresa arranque : pool) {
            PropuestaConsorcio propuesta = construirGreedy(ctx, arranque);
            if (propuesta.requisitosCubiertos().isEmpty() && !propuesta.cumpleGlobal()) {
                continue; // no aporta nada útil
            }
            Set<Long> clave = propuesta.integrantes().stream()
                    .filter(i -> !i.solicitante())
                    .map(IntegrantePropuesto::empresaId)
                    .collect(java.util.stream.Collectors.toSet());
            if (conjuntosVistos.add(clave)) {
                propuestas.add(propuesta);
            }
        }

        propuestas.sort(Comparator
                .comparing((PropuestaConsorcio p) -> !p.cumpleGlobal())      // viables primero
                .thenComparing(p -> p.integrantes().size())                  // menos integrantes
                .thenComparing(p -> -p.requisitosCubiertos().size()));       // más cobertura

        return propuestas.stream().limit(maxPropuestas).toList();
    }

    /** Greedy: arranca con un socio fijo y agrega el que más déficit cubra sin degradar. */
    private PropuestaConsorcio construirGreedy(Contexto ctx, Empresa arranque) {
        List<Empresa> seleccionadas = new ArrayList<>();
        seleccionadas.add(arranque);
        ResultadoEvaluacion resultado = evaluar(ctx, seleccionadas);

        while (!resultado.cumpleGlobal() && seleccionadas.size() < MAX_INTEGRANTES - 1) {
            Empresa mejor = null;
            ResultadoEvaluacion mejorResultado = null;
            int mejorGanancia = 0;

            for (Empresa candidata : ctx.pool()) {
                if (contiene(seleccionadas, candidata)) {
                    continue;
                }
                List<Empresa> tentativa = new ArrayList<>(seleccionadas);
                tentativa.add(candidata);
                ResultadoEvaluacion tentativo = evaluar(ctx, tentativa);
                if (degrada(resultado, tentativo)) {
                    continue;
                }
                int ganancia = contarCumplidos(tentativo) - contarCumplidos(resultado);
                if (ganancia > mejorGanancia) {
                    mejorGanancia = ganancia;
                    mejor = candidata;
                    mejorResultado = tentativo;
                }
            }

            if (mejor == null) {
                break; // ninguna incorporación mejora
            }
            seleccionadas.add(mejor);
            resultado = mejorResultado;
        }

        return toPropuesta(ctx, seleccionadas, resultado);
    }

    private ResultadoEvaluacion evaluar(Contexto ctx, List<Empresa> candidatas) {
        return analizador.evaluarConsorcio(construirIntegrantes(ctx, candidatas), ctx.requisito());
    }

    private List<IntegranteEvaluacion> construirIntegrantes(Contexto ctx, List<Empresa> candidatas) {
        Map<Long, BigDecimal> reparto = repartoDe(ctx, candidatas);
        List<IntegranteEvaluacion> integrantes = new ArrayList<>();
        integrantes.add(new IntegranteEvaluacion(ctx.solicitante(), ctx.porcentajeSolicitante()));
        for (Empresa c : candidatas) {
            integrantes.add(new IntegranteEvaluacion(c, reparto.get(c.getId())));
        }
        return integrantes;
    }

    private Map<Long, BigDecimal> repartoDe(Contexto ctx, List<Empresa> candidatas) {
        Map<Long, BigDecimal> pesos = new LinkedHashMap<>();
        for (Empresa c : candidatas) {
            pesos.put(c.getId(), ctx.pesos().getOrDefault(c.getId(), BigDecimal.ZERO));
        }
        return RepartidorParticipacion.repartir(ctx.complemento(), pesos);
    }

    private PropuestaConsorcio toPropuesta(Contexto ctx, List<Empresa> candidatas, ResultadoEvaluacion resultado) {
        List<String> noCumplidos = nombresNoCumplidos(resultado);
        List<String> cubiertos = ctx.deficit().stream().filter(r -> !noCumplidos.contains(r)).toList();
        List<String> pendientes = ctx.deficit().stream().filter(noCumplidos::contains).toList();

        Map<Long, BigDecimal> reparto = repartoDe(ctx, candidatas);
        List<IntegrantePropuesto> integrantes = new ArrayList<>();
        integrantes.add(new IntegrantePropuesto(
                ctx.solicitante().getId(), ctx.solicitante().getNit(), ctx.solicitante().getRazonSocial(),
                ctx.porcentajeSolicitante(), true, List.of()));
        for (Empresa c : candidatas) {
            integrantes.add(new IntegrantePropuesto(
                    c.getId(), c.getNit(), c.getRazonSocial(),
                    reparto.get(c.getId()), false,
                    ctx.fortalezas().getOrDefault(c.getId(), List.of())));
        }
        return new PropuestaConsorcio(integrantes, resultado.cumpleGlobal(), cubiertos, pendientes);
    }

    /**
     * Precalcula, una sola vez por análisis, el peso de aporte y las fortalezas de cada
     * candidata respecto al déficit del solicitante. El peso es adimensional: suma de
     * (valorActual / valorRequerido) sobre los rubros deficitarios, lo que permite comparar
     * rubros en distintas unidades (pesos, SMMLV, ratios).
     */
    private Contexto construirContexto(Empresa solicitante, List<Empresa> pool, RequisitoLicitacion requisito,
                                       BigDecimal porcentajeSolicitante, List<String> deficit) {
        Map<Long, BigDecimal> pesos = new java.util.HashMap<>();
        Map<Long, List<String>> fortalezas = new java.util.HashMap<>();

        for (Empresa c : pool) {
            ResultadoEvaluacion ind = analizador.analizar(c, requisito, TipoParticipacion.INDIVIDUAL);
            BigDecimal score = BigDecimal.ZERO;
            List<String> fuertesEn = new ArrayList<>();
            for (DetalleRequisito d : ind.detalles()) {
                if (!deficit.contains(d.nombre())) {
                    continue;
                }
                if (d.cumple()) {
                    fuertesEn.add(d.nombre());
                }
                if (d.valorRequerido() != null && d.valorRequerido().signum() > 0 && d.valorActual() != null) {
                    score = score.add(d.valorActual().divide(d.valorRequerido(), ESCALA, RoundingMode.HALF_UP));
                }
            }
            pesos.put(c.getId(), score);
            fortalezas.put(c.getId(), fuertesEn);
        }

        BigDecimal complemento = BigDecimal.ONE.subtract(porcentajeSolicitante);
        return new Contexto(solicitante, pool, requisito, porcentajeSolicitante, complemento, deficit, pesos, fortalezas);
    }

    private static boolean degrada(ResultadoEvaluacion antes, ResultadoEvaluacion despues) {
        Map<String, Boolean> cumpleDespues = new java.util.HashMap<>();
        for (DetalleRequisito d : despues.detalles()) {
            cumpleDespues.put(d.nombre(), d.cumple());
        }
        for (DetalleRequisito d : antes.detalles()) {
            if (d.cumple() && Boolean.FALSE.equals(cumpleDespues.get(d.nombre()))) {
                return true;
            }
        }
        return false;
    }

    private static int contarCumplidos(ResultadoEvaluacion resultado) {
        return (int) resultado.detalles().stream().filter(DetalleRequisito::cumple).count();
    }

    private static List<String> nombresNoCumplidos(ResultadoEvaluacion resultado) {
        return resultado.detalles().stream()
                .filter(d -> !d.cumple())
                .map(DetalleRequisito::nombre)
                .toList();
    }

    private static boolean contiene(List<Empresa> empresas, Empresa empresa) {
        return empresas.stream().anyMatch(e -> e.getId().equals(empresa.getId()));
    }

    /** Estado inmutable compartido durante la búsqueda para un análisis concreto. */
    private record Contexto(
            Empresa solicitante,
            List<Empresa> pool,
            RequisitoLicitacion requisito,
            BigDecimal porcentajeSolicitante,
            BigDecimal complemento,
            List<String> deficit,
            Map<Long, BigDecimal> pesos,
            Map<Long, List<String>> fortalezas
    ) {}
}
