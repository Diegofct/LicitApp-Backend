package com.elemental.licitapp.CuadroDeObra.application.service;

import com.elemental.licitapp.CuadroDeObra.application.ports.in.ConsultarCuadrosUseCase;
import com.elemental.licitapp.CuadroDeObra.application.ports.in.ConsultarRequisitosUseCase;
import com.elemental.licitapp.CuadroDeObra.application.ports.in.CuadroDeObraUseCase;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.CuadroDeObraRepositoryPort;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.ExisteConformacionConsorcioPort;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.InicializarSeguimientoPort;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.RequisitoLicitacionRepositoryPort;
import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.CuadroDeObra.domain.enums.PresentacionMarca;
import com.elemental.licitapp.CuadroDeObra.domain.projection.CuadroDeObraRef;
import com.elemental.licitapp.Exception.ProcesoYaRegistradoException;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class CuadroDeObraService implements CuadroDeObraUseCase, ConsultarRequisitosUseCase, ConsultarCuadrosUseCase {

    private final CuadroDeObraRepositoryPort cuadroDeObraRepositoryPort;
    private final RequisitoLicitacionRepositoryPort requisitoRepositoryPort;
    private final InicializarSeguimientoPort inicializarSeguimientoPort;
    private final ExisteConformacionConsorcioPort existeConformacionConsorcioPort;

    public CuadroDeObraService(CuadroDeObraRepositoryPort cuadroDeObraRepositoryPort,
                               RequisitoLicitacionRepositoryPort requisitoRepositoryPort,
                               InicializarSeguimientoPort inicializarSeguimientoPort,
                               ExisteConformacionConsorcioPort existeConformacionConsorcioPort) {
        this.cuadroDeObraRepositoryPort = cuadroDeObraRepositoryPort;
        this.requisitoRepositoryPort = requisitoRepositoryPort;
        this.inicializarSeguimientoPort = inicializarSeguimientoPort;
        this.existeConformacionConsorcioPort = existeConformacionConsorcioPort;
    }

    @Override
    @Transactional
    public RequisitoLicitacion saveRequisito(Long cuadroId, RequisitoLicitacion requisito) {
        CuadroDeObra cuadro = findCuadroById(cuadroId);
        requisito.setCuadroDeObra(cuadro);
        return requisitoRepositoryPort.save(requisito);
    }

    @Override
    @Transactional
    public RequisitoLicitacion actualizarRequisito(Long cuadroId, RequisitoLicitacion parche) {
        RequisitoLicitacion existente = requisitoRepositoryPort.findByCuadroDeObraId(cuadroId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe requisito para el cuadro de obra con id: " + cuadroId));
        existente.aplicarPatch(parche);
        return requisitoRepositoryPort.save(existente);
    }

    @Override
    @Transactional(readOnly = true)
    public RequisitoLicitacion getRequisitoByCuadroId(Long cuadroId) {
        return requisitoRepositoryPort.findByCuadroDeObraId(cuadroId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontraron requisitos para este proceso"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RequisitoLicitacion> obtenerPorCuadroId(Long cuadroId) {
        return requisitoRepositoryPort.findByCuadroDeObraId(cuadroId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> cuadrosConRequisitos(Collection<Long> ids) {
        return requisitoRepositoryPort.idsConRequisitos(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<CuadroDeObraEstado, Long> contarPorEstado() {
        return cuadroDeObraRepositoryPort.contarPorEstado();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CuadroDeObra> listarPorEstados(List<CuadroDeObraEstado> estados, Pageable pageable) {
        return cuadroDeObraRepositoryPort.findByCuadroDeObraEstadoIn(estados, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CuadroDeObra> findCuadrosPorVistas(String vista, Pageable pageable){
        List<CuadroDeObraEstado> estados;

        if("presentadas".equalsIgnoreCase(vista)){
            estados = List.of(CuadroDeObraEstado.PRESENTADO, CuadroDeObraEstado.ADJUDICADO, CuadroDeObraEstado.NO_ADJUDICADO, CuadroDeObraEstado.CANCELADO);
        } else {
            estados = List.of(CuadroDeObraEstado.POR_PRESENTAR);
        }
        return cuadroDeObraRepositoryPort.findByCuadroDeObraEstadoIn(estados, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CuadroDeObra findCuadroById(Long id) {
        return cuadroDeObraRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuadro de obra con id: " + id + "no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuadroDeObraRef> obtenerReferencias() {
        return cuadroDeObraRepositoryPort.obtenerReferencias();
    }

    @Override
    @Transactional
    public CuadroDeObra createCuadro (CuadroDeObra nuevoCuadro){
        // Evita agregar dos veces la misma licitación al Cuadro de Obra. El cruce va por
        // idDelProceso y no por numeroProceso: este último se repite entre entidades, así
        // que rechazaba procesos distintos que solo compartían el número.
        // Los cuadros cargados a mano no vienen de SECOP y no traen idDelProceso: no
        // aplica la restricción de unicidad.
        String idDelProceso = nuevoCuadro.getIdDelProceso();
        if (idDelProceso != null && !idDelProceso.isBlank()
                && cuadroDeObraRepositoryPort.existePorIdDelProceso(idDelProceso)) {
            // El mensaje usa el número de proceso: es lo que el analista reconoce.
            throw new ProcesoYaRegistradoException(
                    "Ya existe un cuadro de obra para el proceso " + nuevoCuadro.getNumeroProceso());
        }
        nuevoCuadro.setCuadroDeObraEstado(CuadroDeObraEstado.POR_PRESENTAR);
        return cuadroDeObraRepositoryPort.save(nuevoCuadro);
    }

    @Override
    @Transactional
    public CuadroDeObra updateCuadro(Long id, CuadroDeObra cuadroConActualizaciones){
        CuadroDeObra cuadroExistente = findCuadroById(id);

        // fechaPublicacion es inmutable: una vez publicada por la entidad contratante en SECOP, no se modifica.
        // idDelProceso tampoco se toca: es la identidad del cuadro frente a SECOP, no un dato editable.
        cuadroExistente.setEntidadContratante(cuadroConActualizaciones.getEntidadContratante());
        cuadroExistente.setNumeroProceso(cuadroConActualizaciones.getNumeroProceso());
        cuadroExistente.setDescripcionObjeto(cuadroConActualizaciones.getDescripcionObjeto());
        cuadroExistente.setEstadoProceso(cuadroConActualizaciones.getEstadoProceso());
        cuadroExistente.setFechaCierre(cuadroConActualizaciones.getFechaCierre());
        cuadroExistente.setMonto(cuadroConActualizaciones.getMonto());
        cuadroExistente.setValorSMMLV(cuadroConActualizaciones.getValorSMMLV());
        cuadroExistente.setTipoProyecto(cuadroConActualizaciones.getTipoProyecto());
        cuadroExistente.setDepartamento(cuadroConActualizaciones.getDepartamento());
        cuadroExistente.setMunicipio(cuadroConActualizaciones.getMunicipio());
        cuadroExistente.setExperiencia(cuadroConActualizaciones.getExperiencia());
        cuadroExistente.setPlazo(cuadroConActualizaciones.getPlazo());
        cuadroExistente.setAnticipo(cuadroConActualizaciones.getAnticipo());
        cuadroExistente.setObservacion(cuadroConActualizaciones.getObservacion());
        boolean transicionoAPresentado = aplicarCambioDeEstado(
                cuadroExistente, cuadroConActualizaciones.getCuadroDeObraEstado());

        CuadroDeObra guardado = cuadroDeObraRepositoryPort.save(cuadroExistente);
        if (transicionoAPresentado) {
            inicializarSeguimientoPort.inicializar(guardado.getId());
        }
        return guardado;
    }

    @Override
    @Transactional
    public CuadroDeObra updateEstado(Long id, CuadroDeObraEstado nuevoEstado){
        CuadroDeObra cuadroExistente = findCuadroById(id);
        boolean transicionoAPresentado = aplicarCambioDeEstado(cuadroExistente, nuevoEstado);
        CuadroDeObra guardado = cuadroDeObraRepositoryPort.save(cuadroExistente);
        if (transicionoAPresentado) {
            inicializarSeguimientoPort.inicializar(guardado.getId());
        }
        return guardado;
    }

    /**
     * @return true si la transición resultó en CuadroDeObraEstado.PRESENTADO (caller
     *         debe disparar la inicialización del seguimiento).
     */
    private boolean aplicarCambioDeEstado(CuadroDeObra cuadro, CuadroDeObraEstado nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado == cuadro.getCuadroDeObraEstado()) {
            return false;
        }
        if (!cuadro.getCuadroDeObraEstado().puedeTransicionarA(nuevoEstado)) {
            throw new IllegalArgumentException(
                    "Transición de estado inválida: " + cuadro.getCuadroDeObraEstado() + " → " + nuevoEstado);
        }
        if (nuevoEstado == CuadroDeObraEstado.PRESENTADO
                && !existeConformacionConsorcioPort.existePara(cuadro.getId())) {
            throw new IllegalArgumentException(
                    "No se puede pasar el cuadro " + cuadro.getId() + " a PRESENTADO sin antes guardar "
                            + "la conformación del proponente (POST /analisis/consorcios). Si se presentó "
                            + "individualmente, registra una conformación tipo INDIVIDUAL al 100%.");
        }
        cuadro.setCuadroDeObraEstado(nuevoEstado);
        return nuevoEstado == CuadroDeObraEstado.PRESENTADO;
    }

    @Override
    @Transactional
    public CuadroDeObra actualizarPresentacion(Long id, PresentacionMarca marca) {
        // marca == null limpia la marca (vuelve a "sin marca"): es un valor válido.
        CuadroDeObra cuadro = findCuadroById(id);
        cuadro.setPresentacion(marca);
        return cuadroDeObraRepositoryPort.save(cuadro);
    }

    @Override
    @Transactional
    public void deleteCuadro(Long id){
        findCuadroById(id);
        requisitoRepositoryPort.deleteByCuadroDeObraId(id);
        cuadroDeObraRepositoryPort.delete(id);
    }

}