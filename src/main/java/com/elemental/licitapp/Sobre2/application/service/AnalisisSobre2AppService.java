package com.elemental.licitapp.Sobre2.application.service;

import com.elemental.licitapp.Exception.ResourceNotFoundException;
import com.elemental.licitapp.Sobre2.application.ports.in.AnalizarSobre2UseCase;
import com.elemental.licitapp.Sobre2.application.ports.out.DatosProcesoPort;
import com.elemental.licitapp.Sobre2.application.ports.out.MomentoSobre2Port;
import com.elemental.licitapp.Sobre2.application.ports.out.OferenteProcesoRepositoryPort;
import com.elemental.licitapp.Sobre2.domain.entity.AnalisisSobre2;
import com.elemental.licitapp.Sobre2.domain.entity.DatosProceso;
import com.elemental.licitapp.Sobre2.domain.entity.MomentoSobre2;
import com.elemental.licitapp.Sobre2.domain.entity.OferenteProceso;
import com.elemental.licitapp.Sobre2.domain.entity.ResultadoMetodo;
import com.elemental.licitapp.Sobre2.domain.enums.MetodoPonderacion;
import com.elemental.licitapp.Sobre2.domain.enums.RegimenPonderacion;
import com.elemental.licitapp.Sobre2.domain.service.CalculadoraPonderacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Orquesta el motor de ponderacion: arma la muestra de ofertas validas, delega el calculo
 * en el dominio puro y compone el resultado con las advertencias que el analista necesita.
 */
@Service
public class AnalisisSobre2AppService implements AnalizarSobre2UseCase {

    private static final BigDecimal PUNTAJE_MAXIMO_DEFECTO = BigDecimal.valueOf(100);

    private final OferenteProcesoRepositoryPort repositorio;
    private final DatosProcesoPort datosProceso;
    private final MomentoSobre2Port momento;

    public AnalisisSobre2AppService(OferenteProcesoRepositoryPort repositorio,
                                    DatosProcesoPort datosProceso,
                                    MomentoSobre2Port momento) {
        this.repositorio = repositorio;
        this.datosProceso = datosProceso;
        this.momento = momento;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalisisSobre2 analizar(Long cuadroId,
                                   RegimenPonderacion regimen,
                                   BigDecimal valorCandidato,
                                   BigDecimal puntajeMaximo) {

        DatosProceso proceso = datosProceso.obtener(cuadroId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuadro de obra con id: " + cuadroId + " no encontrado"));

        RegimenPonderacion regimenEfectivo = regimen != null ? regimen : RegimenPonderacion.DOCUMENTOS_TIPO;
        BigDecimal puntajeTope = puntajeMaximo != null && puntajeMaximo.signum() > 0
                ? puntajeMaximo
                : PUNTAJE_MAXIMO_DEFECTO;

        BigDecimal presupuesto = proceso.presupuestoOficial();

        List<OferenteProceso> todos = repositorio.findByCuadroId(cuadroId);
        List<OferenteProceso> validos = todos.stream()
                .filter(o -> o.participaEnLasFormulas(presupuesto))
                .toList();

        // Las dos causas de exclusion se cuentan de forma disjunta: una oferta puede ser a la
        // vez no valida y estar por encima del presupuesto, y no debe reportarse dos veces.
        long noValidas = todos.stream().filter(o -> !esUtilizable(o)).count();
        long sobreElPresupuesto = todos.stream()
                .filter(AnalisisSobre2AppService::esUtilizable)
                .filter(o -> o.superaElPresupuesto(presupuesto))
                .count();

        if (validos.isEmpty()) {
            throw new IllegalArgumentException(sobreElPresupuesto > 0 && noValidas == 0
                    ? "Las " + todos.size() + " oferta(s) cargadas superan el presupuesto oficial, que es causal "
                            + "de rechazo: no queda muestra con la que calcular los metodos de ponderacion. "
                            + "Revisa que el presupuesto oficial del cuadro sea el del pliego definitivo."
                    : "El proceso no tiene oferentes validos cargados: importa desde SECOP o cargalos a mano "
                            + "antes de calcular los metodos de ponderacion.");
        }

        // El momento del proceso va primero: condiciona como se lee todo lo que sigue.
        // Nunca bloquea, solo advierte (el modulo tambien se usa sobre procesos historicos).
        MomentoSobre2 momentoProceso = momento.obtener(cuadroId);
        List<String> advertencias = new ArrayList<>();
        AvisosMomentoSobre2.agregarSegun(advertencias, momentoProceso);

        if (presupuesto == null || presupuesto.signum() <= 0) {
            advertencias.add("El proceso no tiene presupuesto oficial: no se calculan porcentajes, se omite "
                    + "la media geometrica con presupuesto oficial y no se pueden descartar las ofertas que "
                    + "lo superen.");
        }
        if (validos.size() < 3) {
            advertencias.add("Solo hay " + validos.size() + " oferta(s) valida(s): la muestra es demasiado "
                    + "pequena para que las referencias sean representativas.");
        }
        if (sobreElPresupuesto > 0) {
            advertencias.add(sobreElPresupuesto + " oferta(s) superan el presupuesto oficial y quedaron fuera "
                    + "de la muestra: superarlo es causal de rechazo, asi que esas ofertas no compiten y no "
                    + "deben mover las medidas de tendencia.");
        }
        if (noValidas > 0) {
            advertencias.add(noValidas + " oferta(s) quedaron fuera de las formulas por estar marcadas como "
                    + "no validas.");
        }

        // Muestra de referencia: las ofertas de la competencia, sin la propia.
        List<BigDecimal> muestraBase = CalculadoraPonderacion.normalizar(
                validos.stream().map(OferenteProceso::getValorOferta).toList());

        // Muestra de evaluacion: incluye el valor candidato, porque presentarse cambia la
        // referencia que se quiere alcanzar.
        List<BigDecimal> muestraConCandidato = valorCandidato != null
                ? CalculadoraPonderacion.normalizar(concat(muestraBase, valorCandidato))
                : muestraBase;

        List<MetodoPonderacion> metodos = MetodoPonderacion.delRegimen(regimenEfectivo).stream()
                .filter(m -> !m.requierePresupuestoOficial() || (presupuesto != null && presupuesto.signum() > 0))
                .toList();

        // Primera pasada: referencias sobre la muestra SIN el candidato. Incluirlo seria
        // circular (el candidato desplaza la referencia que intenta alcanzar). De aqui sale
        // la sugerencia y, con ella, a que tendencia se le esta apuntando.
        List<CalculadoraPonderacion.ReferenciaDeMetodo> referenciasParaSugerir = metodos.stream()
                .map(m -> new CalculadoraPonderacion.ReferenciaDeMetodo(
                        m, CalculadoraPonderacion.valorReferencia(m, muestraBase, presupuesto)))
                .toList();

        CalculadoraPonderacion.ReferenciaDeMetodo sugerencia =
                CalculadoraPonderacion.referenciaSugerida(referenciasParaSugerir);
        BigDecimal sugerido = sugerencia != null ? sugerencia.valor() : null;

        // Presentarse cambia la muestra, asi que el puntaje del sugerido se evalua con el
        // sugerido dentro, igual que se hace con el candidato.
        List<BigDecimal> muestraConSugerido = sugerido != null
                ? CalculadoraPonderacion.normalizar(concat(muestraBase, sugerido))
                : muestraBase;

        // Segunda pasada: los resultados por metodo, ya con el sugerido conocido.
        List<ResultadoMetodo> resultados = new ArrayList<>();
        for (MetodoPonderacion metodo : metodos) {
            BigDecimal referenciaEval =
                    CalculadoraPonderacion.valorReferencia(metodo, muestraConCandidato, presupuesto);
            BigDecimal objetivo =
                    CalculadoraPonderacion.valorObjetivo(metodo, muestraConCandidato, referenciaEval);

            BigDecimal puntajeCandidato = null;
            Integer posicion = null;
            if (valorCandidato != null) {
                puntajeCandidato = CalculadoraPonderacion.puntaje(
                        metodo, valorCandidato, muestraConCandidato, referenciaEval, puntajeTope);
                posicion = posicionDelCandidato(metodo, valorCandidato, validos,
                        muestraConCandidato, referenciaEval, puntajeTope);
            }

            BigDecimal puntajeSugerido = null;
            if (sugerido != null) {
                BigDecimal referenciaConSugerido =
                        CalculadoraPonderacion.valorReferencia(metodo, muestraConSugerido, presupuesto);
                puntajeSugerido = CalculadoraPonderacion.puntaje(
                        metodo, sugerido, muestraConSugerido, referenciaConSugerido, puntajeTope);
            }

            resultados.add(new ResultadoMetodo(
                    metodo,
                    referenciaEval,
                    CalculadoraPonderacion.porcentaje(referenciaEval, presupuesto),
                    objetivo,
                    nombreDelMasCercano(validos, objetivo),
                    puntajeCandidato,
                    posicion,
                    puntajeSugerido));
        }

        return new AnalisisSobre2(
                cuadroId,
                proceso.numeroProceso(),
                presupuesto,
                regimenEfectivo,
                todos.size(),
                validos.size(),
                puntajeTope,
                valorCandidato != null ? valorCandidato.setScale(2, RoundingMode.DOWN) : null,
                CalculadoraPonderacion.porcentaje(valorCandidato, presupuesto),
                sugerido,
                CalculadoraPonderacion.porcentaje(sugerido, presupuesto),
                resultados,
                advertencias,
                momentoProceso.estadoCuadro(),
                momentoProceso.listoParaDecidir(),
                momentoProceso.valoresDefinitivos(),
                sugerencia != null ? sugerencia.metodo() : null);
    }

    /** Utilizable = el analista no la descarto y trae un valor positivo. */
    private static boolean esUtilizable(OferenteProceso oferente) {
        return Boolean.TRUE.equals(oferente.getValida())
                && oferente.getValorOferta() != null
                && oferente.getValorOferta().signum() > 0;
    }

    /** Puesto del candidato por puntaje entre todas las ofertas (1 = mejor). */
    private int posicionDelCandidato(MetodoPonderacion metodo,
                                     BigDecimal valorCandidato,
                                     List<OferenteProceso> competencia,
                                     List<BigDecimal> muestra,
                                     BigDecimal referencia,
                                     BigDecimal puntajeTope) {
        BigDecimal puntajeCandidato = CalculadoraPonderacion.puntaje(
                metodo, valorCandidato, muestra, referencia, puntajeTope);
        long mejores = competencia.stream()
                .map(o -> CalculadoraPonderacion.puntaje(
                        metodo, o.getValorOferta(), muestra, referencia, puntajeTope))
                .filter(p -> p.compareTo(puntajeCandidato) > 0)
                .count();
        return (int) mejores + 1;
    }

    private String nombreDelMasCercano(List<OferenteProceso> oferentes, BigDecimal objetivo) {
        return oferentes.stream()
                .min(Comparator.comparing(o -> o.getValorOferta().subtract(objetivo).abs()))
                .map(OferenteProceso::getNombreOferente)
                .orElse(null);
    }

    private static List<BigDecimal> concat(List<BigDecimal> valores, BigDecimal extra) {
        List<BigDecimal> copia = new ArrayList<>(valores);
        copia.add(extra);
        return copia;
    }
}
