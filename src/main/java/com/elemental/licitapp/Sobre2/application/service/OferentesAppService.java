package com.elemental.licitapp.Sobre2.application.service;

import com.elemental.licitapp.Exception.ResourceNotFoundException;
import com.elemental.licitapp.Sobre2.application.ports.in.ConsultarCompetidoresUseCase;
import com.elemental.licitapp.Sobre2.application.ports.in.GestionarOferentesUseCase;
import com.elemental.licitapp.Sobre2.application.ports.in.ImportarOferentesUseCase;
import com.elemental.licitapp.Sobre2.application.ports.out.DatosProcesoPort;
import com.elemental.licitapp.Sobre2.application.ports.out.MomentoSobre2Port;
import com.elemental.licitapp.Sobre2.application.ports.out.OferenteProcesoRepositoryPort;
import com.elemental.licitapp.Sobre2.application.ports.out.OfertasSecopPort;
import com.elemental.licitapp.Sobre2.application.ports.out.ProcesoSecopPort;
import com.elemental.licitapp.Sobre2.domain.entity.CriterioBusquedaOfertas;
import com.elemental.licitapp.Sobre2.domain.entity.DatosProceso;
import com.elemental.licitapp.Sobre2.domain.entity.OferenteProceso;
import com.elemental.licitapp.Sobre2.domain.entity.OfertaImportada;
import com.elemental.licitapp.Sobre2.domain.entity.ProcesoSecop;
import com.elemental.licitapp.Sobre2.domain.entity.ResumenCompetidor;
import com.elemental.licitapp.Sobre2.domain.enums.OrigenOferente;
import com.elemental.licitapp.Sobre2.domain.service.CalculadoraPonderacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Importacion desde SECOP II, gestion manual de oferentes e inteligencia de competidores.
 */
@Service
public class OferentesAppService
        implements ImportarOferentesUseCase, GestionarOferentesUseCase, ConsultarCompetidoresUseCase {

    private static final String MONEDA_COP = "COP";

    private final OferenteProcesoRepositoryPort repositorio;
    private final OfertasSecopPort ofertasSecop;
    private final ProcesoSecopPort procesoSecop;
    private final DatosProcesoPort datosProceso;
    private final MomentoSobre2Port momento;

    public OferentesAppService(OferenteProcesoRepositoryPort repositorio,
                               OfertasSecopPort ofertasSecop,
                               ProcesoSecopPort procesoSecop,
                               DatosProcesoPort datosProceso,
                               MomentoSobre2Port momento) {
        this.repositorio = repositorio;
        this.ofertasSecop = ofertasSecop;
        this.procesoSecop = procesoSecop;
        this.datosProceso = datosProceso;
        this.momento = momento;
    }

    @Override
    @Transactional
    public ResultadoImportacion importar(Long cuadroId, String nitEntidad, boolean historico) {
        DatosProceso proceso = exigirProceso(cuadroId);
        List<String> advertencias = new ArrayList<>();

        // La fiabilidad de lo que se acaba de traer depende de si el Sobre 2 ya se abrio.
        // Se avisa aqui y no solo en el analisis para que el dato llegue en el momento en
        // que el analista mira las cifras por primera vez. Nunca bloquea la importacion:
        // importar procesos historicos o ajenos es un uso valido del modulo.
        if (!momento.obtener(cuadroId).valoresDefinitivos()) {
            AvisosMomentoSobre2.agregarSiFalta(advertencias, AvisosMomentoSobre2.SOBRE_2_SIN_ABRIR);
        }

        if (proceso.presupuestoOficial() == null || proceso.presupuestoOficial().signum() <= 0) {
            advertencias.add("El proceso no tiene presupuesto oficial (ni en requisitos ni en el monto del cuadro): "
                    + "no se pudo calcular el porcentaje de cada oferta.");
        }

        CriterioBusquedaOfertas criterio = resolverCriterio(proceso, nitEntidad, advertencias);
        List<OfertaImportada> ofertas = ofertasSecop.buscarOfertas(criterio, historico);

        purgarOfertasObsoletas(cuadroId, criterio, ofertas, advertencias);

        if (ofertas.isEmpty()) {
            Integer proponentes = contarProponentes(criterio);
            advertencias.add(mensajeSinOfertas(cuadroId, criterio, proponentes));
            return new ResultadoImportacion(cuadroId, 0, 0, 0,
                    repositorio.findByCuadroId(cuadroId), advertencias, proponentes);
        }

        int creados = 0;
        int actualizados = 0;
        LocalDateTime ahora = LocalDateTime.now();
        for (OfertaImportada oferta : ofertas) {
            OferenteProceso existente = repositorio
                    .findByCuadroIdEIdentificador(cuadroId, oferta.identificadorOferta())
                    .orElse(null);
            if (existente == null) {
                repositorio.save(nuevoDesdeSecop(cuadroId, oferta, proceso.presupuestoOficial(), ahora));
                creados++;
            } else {
                aplicarDatosDeSecop(existente, oferta, proceso.presupuestoOficial(), ahora);
                repositorio.save(existente);
                actualizados++;
            }
        }

        long enOtraMoneda = ofertas.stream().filter(o -> !esCop(o.moneda())).count();
        if (enOtraMoneda > 0) {
            advertencias.add(enOtraMoneda + " oferta(s) vienen en una moneda distinta a COP. Quedaron marcadas "
                    + "como no validas para que no distorsionen las formulas; revisalas antes de usarlas.");
        }

        BigDecimal presupuesto = proceso.presupuestoOficial();
        if (presupuesto != null && presupuesto.signum() > 0) {
            long sobreElPresupuesto = ofertas.stream()
                    .filter(o -> o.valorOferta() != null && o.valorOferta().compareTo(presupuesto) > 0)
                    .count();
            if (sobreElPresupuesto > 0) {
                advertencias.add(sobreElPresupuesto + " oferta(s) superan el presupuesto oficial, que es causal "
                        + "de rechazo. Quedan registradas pero el analisis las deja fuera de la muestra para que "
                        + "no sesguen las medidas de tendencia.");
            }
        }

        return new ResultadoImportacion(cuadroId, ofertas.size(), creados, actualizados,
                repositorio.findByCuadroId(cuadroId), advertencias, null);
    }

    /**
     * Elimina las filas de origen SECOP que ya no corresponden al proceso.
     *
     * <p>Solo se purga cuando se cruzo <b>por identificador</b>, porque ese cruce es
     * autoritativo: lo que SECOP devuelve para ese portafolio es el conjunto completo de
     * ofertas del proceso, asi que una fila SECOP que no este en el resultado no pertenece
     * aqui. Tipicamente quedo de un cruce anterior por referencia, que no es unica entre
     * entidades y arrastra ofertas de procesos ajenos.
     *
     * <p>En la rama degradada <b>no se purga nada</b>: si el criterio no distingue procesos,
     * su resultado tampoco autoriza a borrar.
     *
     * <p>Se borra por diferencia y no en bloque para no perder el trabajo del analista: las
     * ofertas que siguen viniendo se actualizan y conservan su flag {@code valida}, de modo
     * que reimportar no revive una oferta que se habia marcado como no valida. Las filas
     * MANUAL nunca se tocan: son suyas.
     *
     * <p>Un resultado vacio tambien purga, y es intencional: "SECOP no publica ofertas para
     * este proceso" es una respuesta, no un fallo. Los fallos de transporte no llegan aqui
     * porque {@code buscarOfertas} lanza {@code SecopApiException} en vez de devolver vacio.
     */
    private void purgarOfertasObsoletas(Long cuadroId,
                                        CriterioBusquedaOfertas criterio,
                                        List<OfertaImportada> ofertas,
                                        List<String> advertencias) {
        if (!(criterio instanceof CriterioBusquedaOfertas.PorIdDeProcesoDeCompra)) {
            return;
        }

        Set<String> vigentes = ofertas.stream()
                .map(OfertaImportada::identificadorOferta)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Long> obsoletas = repositorio.findByCuadroId(cuadroId).stream()
                .filter(o -> o.getOrigen() == OrigenOferente.SECOP)
                .filter(o -> !vigentes.contains(o.getIdentificadorOferta()))
                .map(OferenteProceso::getId)
                .toList();

        if (obsoletas.isEmpty()) {
            return;
        }

        repositorio.deleteAllById(obsoletas);
        advertencias.add(obsoletas.size() + " oferta(s) que estaban guardadas no corresponden a este proceso "
                + "y se eliminaron: venian de una importacion anterior que buscaba por referencia, que no es "
                + "unica entre entidades. Las ofertas agregadas a mano no se tocaron.");
    }

    /** Solo tiene sentido preguntarlo cuando se busco por identificador y no vino nada. */
    private Integer contarProponentes(CriterioBusquedaOfertas criterio) {
        if (criterio instanceof CriterioBusquedaOfertas.PorIdDeProcesoDeCompra(String portafolio)) {
            return procesoSecop.contarProponentes(portafolio).orElse(null);
        }
        return null;
    }

    /**
     * Que se le dice al analista cuando no llegaron valores. Son tres situaciones muy
     * distintas y confundirlas le hace perder tiempo:
     *
     * <ul>
     *   <li><b>Hay proponentes pero sin valores</b>: el Sobre 2 sigue sellado. No hay nada
     *       que arreglar; hay que esperar la audiencia. Es el caso mas frecuente cuando se
     *       consulta un proceso todavia en evaluacion.</li>
     *   <li><b>Cero proponentes</b>: el proceso quedo desierto o aun no cierra.</li>
     *   <li><b>Busqueda por referencia sin resultados</b>: puede ser que SECOP no publique,
     *       pero tambien que la referencia no corresponda al proceso.</li>
     * </ul>
     */
    private static String mensajeSinOfertas(Long cuadroId, CriterioBusquedaOfertas criterio,
                                            Integer proponentes) {
        if (proponentes != null && proponentes > 0) {
            return "SECOP reporta " + proponentes + " proponente(s) en este proceso, pero todavia no "
                    + "publica los valores de sus ofertas: el Sobre 2 no se ha abierto y los precios "
                    + "siguen sellados hasta la audiencia. No es un error ni falta nada por importar; "
                    + "vuelve a importar despues de la apertura.";
        }
        if (proponentes != null) {
            return "SECOP no reporta proponentes en este proceso: puede haber quedado desierto o no "
                    + "haber cerrado todavia la presentacion de ofertas.";
        }
        if (criterio instanceof CriterioBusquedaOfertas.PorIdDeProcesoDeCompra) {
            return "SECOP no publico ofertas para este proceso (pasa en cerca del 10% de los casos). "
                    + "Cargalos a mano con POST /sobre-2/" + cuadroId + "/oferentes.";
        }
        return "La busqueda por referencia no devolvio ofertas. Puede ser que SECOP no las publicara "
                + "o que la referencia no corresponda al proceso. Cargalos a mano con "
                + "POST /sobre-2/" + cuadroId + "/oferentes.";
    }

    /**
     * Decide con que se busca en el dataset de ofertas. El camino por identificador es
     * exacto; el de referencia es aproximado y solo se usa cuando el proceso no existe en
     * SECOP o no se pudo resolver. Degrada y advierte, nunca bloquea: importar procesos
     * historicos o cuadros armados a mano es un uso valido del modulo.
     */
    private CriterioBusquedaOfertas resolverCriterio(DatosProceso proceso, String nitManual,
                                                     List<String> advertencias) {
        String idDelProceso = proceso.idDelProceso();
        if (idDelProceso == null || idDelProceso.isBlank()) {
            advertencias.add("El cuadro no esta asociado a un proceso de SECOP (no tiene id del proceso): la "
                    + "busqueda se hara por referencia, que no es unica entre entidades. Revisa que los "
                    + "oferentes importados correspondan al proceso.");
            return porReferencia(proceso, nitManual, advertencias);
        }

        ProcesoSecop resuelto = procesoSecop.resolver(idDelProceso).orElse(null);
        if (resuelto == null) {
            advertencias.add("SECOP no devolvio el proceso " + idDelProceso + ": la busqueda se hara por "
                    + "referencia, que es aproximada.");
            return porReferencia(proceso, nitManual, advertencias);
        }
        if (!resuelto.tienePortafolio()) {
            advertencias.add("El proceso " + idDelProceso + " no publica identificador de proceso de compra: "
                    + "la busqueda se hara por referencia, que es aproximada.");
            return porReferencia(proceso, primeroNoVacio(nitManual, resuelto.nitEntidad()), advertencias);
        }

        String nitNormalizado = soloDigitos(nitManual);
        if (nitNormalizado != null && !nitNormalizado.equals(resuelto.nitEntidad())) {
            advertencias.add("El NIT indicado (" + nitManual + ") no coincide con el de la entidad del proceso "
                    + "en SECOP (" + resuelto.nitEntidad() + "). Se ignoro, porque la busqueda va por "
                    + "identificador de proceso y no lo necesita; pero revisa que sea el cuadro correcto.");
        }
        return new CriterioBusquedaOfertas.PorIdDeProcesoDeCompra(resuelto.idDelPortafolio());
    }

    private CriterioBusquedaOfertas porReferencia(DatosProceso proceso, String nit, List<String> advertencias) {
        if (proceso.numeroProceso() == null || proceso.numeroProceso().isBlank()) {
            throw new IllegalArgumentException("El cuadro " + proceso.cuadroDeObraId() + " no tiene id del "
                    + "proceso de SECOP ni numero de proceso: no hay con que buscar las ofertas.");
        }
        if (nit == null || nit.isBlank()) {
            advertencias.add("No se indico el NIT de la entidad compradora y no se pudo deducir de SECOP. "
                    + "La referencia del proceso no es unica entre entidades.");
        }
        return new CriterioBusquedaOfertas.PorReferencia(proceso.numeroProceso(), nit);
    }

    private static String primeroNoVacio(String preferido, String alternativa) {
        return preferido != null && !preferido.isBlank() ? preferido : alternativa;
    }

    /**
     * El NIT que teclea el analista puede traer puntos o digito de verificacion; el que
     * devuelve SECOP viene ya en digitos. Se normalizan los dos lados para compararlos.
     */
    private static String soloDigitos(String nit) {
        if (nit == null || nit.isBlank()) {
            return null;
        }
        String digitos = nit.replaceAll("\\D", "");
        return digitos.isEmpty() ? null : digitos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OferenteProceso> listar(Long cuadroId) {
        exigirProceso(cuadroId);
        return repositorio.findByCuadroId(cuadroId);
    }

    @Override
    @Transactional
    public OferenteProceso crear(Long cuadroId, OferenteProceso oferente) {
        DatosProceso proceso = exigirProceso(cuadroId);
        validarValor(oferente.getValorOferta());

        oferente.setId(null);
        oferente.setCuadroDeObraId(cuadroId);
        // El identificador de oferta es de SECOP: las altas manuales lo dejan nulo para no
        // chocar con el UNIQUE (MySQL admite varios NULL en un indice unico).
        oferente.setIdentificadorOferta(null);
        oferente.setOrigen(OrigenOferente.MANUAL);
        oferente.setValida(oferente.getValida() == null || oferente.getValida());
        if (oferente.getMoneda() == null || oferente.getMoneda().isBlank()) {
            oferente.setMoneda(MONEDA_COP);
        }
        oferente.setPorcentaje(
                CalculadoraPonderacion.porcentaje(oferente.getValorOferta(), proceso.presupuestoOficial()));
        return repositorio.save(oferente);
    }

    @Override
    @Transactional
    public OferenteProceso actualizar(Long oferenteId, OferenteProceso cambios) {
        OferenteProceso existente = exigirOferente(oferenteId);
        DatosProceso proceso = exigirProceso(existente.getCuadroDeObraId());

        if (cambios.getNombreOferente() != null && !cambios.getNombreOferente().isBlank()) {
            existente.setNombreOferente(cambios.getNombreOferente().trim());
        }
        if (cambios.getNitOferente() != null) {
            existente.setNitOferente(cambios.getNitOferente().isBlank() ? null : cambios.getNitOferente().trim());
        }
        if (cambios.getValorOferta() != null) {
            validarValor(cambios.getValorOferta());
            existente.setValorOferta(cambios.getValorOferta());
            existente.setPorcentaje(
                    CalculadoraPonderacion.porcentaje(cambios.getValorOferta(), proceso.presupuestoOficial()));
        }
        if (cambios.getValida() != null) {
            existente.setValida(cambios.getValida());
        }
        if (cambios.getMoneda() != null && !cambios.getMoneda().isBlank()) {
            existente.setMoneda(cambios.getMoneda().trim());
        }
        return repositorio.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long oferenteId) {
        exigirOferente(oferenteId);
        repositorio.deleteById(oferenteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumenCompetidor> resumenPorCompetidor(String nombre) {
        return repositorio.resumenPorCompetidor(nombre);
    }

    // ------------------------------------------------------------------

    private OferenteProceso nuevoDesdeSecop(Long cuadroId, OfertaImportada oferta,
                                            BigDecimal presupuestoOficial, LocalDateTime ahora) {
        OferenteProceso nuevo = new OferenteProceso();
        nuevo.setCuadroDeObraId(cuadroId);
        nuevo.setIdentificadorOferta(oferta.identificadorOferta());
        nuevo.setOrigen(OrigenOferente.SECOP);
        aplicarDatosDeSecop(nuevo, oferta, presupuestoOficial, ahora);
        return nuevo;
    }

    /**
     * Reimportar debe ser idempotente: se refrescan los datos que SECOP puede corregir
     * (nombre, valor, porcentaje) pero se respeta el flag {@code valida}, que es una
     * decision del analista sobre ofertas rechazadas por la entidad.
     */
    private void aplicarDatosDeSecop(OferenteProceso destino, OfertaImportada oferta,
                                     BigDecimal presupuestoOficial, LocalDateTime ahora) {
        destino.setReferenciaOferta(oferta.referenciaOferta());
        destino.setNombreOferente(oferta.nombreOferente() != null ? oferta.nombreOferente() : "(sin nombre)");
        destino.setNitOferente(oferta.nitOferente());
        destino.setValorOferta(oferta.valorOferta());
        destino.setMoneda(oferta.moneda());
        destino.setFechaRegistro(oferta.fechaRegistro());
        destino.setPorcentaje(CalculadoraPonderacion.porcentaje(oferta.valorOferta(), presupuestoOficial));
        destino.setFechaImportacion(ahora);
        if (destino.getValida() == null) {
            // Una oferta en moneda distinta a COP no puede mezclarse con las demas en las
            // formulas: entra marcada como no valida y el analista decide.
            destino.setValida(esCop(oferta.moneda()));
        }
    }

    private static boolean esCop(String moneda) {
        return moneda == null || moneda.isBlank() || MONEDA_COP.equalsIgnoreCase(moneda.trim());
    }

    private static void validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("El valor de la oferta debe ser mayor que cero.");
        }
    }

    private DatosProceso exigirProceso(Long cuadroId) {
        return datosProceso.obtener(cuadroId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuadro de obra con id: " + cuadroId + " no encontrado"));
    }

    private OferenteProceso exigirOferente(Long oferenteId) {
        return repositorio.findById(oferenteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Oferente con id: " + oferenteId + " no encontrado"));
    }
}
