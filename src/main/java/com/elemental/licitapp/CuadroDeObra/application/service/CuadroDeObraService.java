package com.elemental.licitapp.CuadroDeObra.application.service;

import com.elemental.licitapp.CuadroDeObra.application.ports.in.CuadroDeObraUseCase;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.CuadroDeObraRepositoryPort;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.RequisitoLicitacionRepositoryPort;
import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CuadroDeObraService implements CuadroDeObraUseCase {

    private final CuadroDeObraRepositoryPort cuadroDeObraRepositoryPort;
    private final RequisitoLicitacionRepositoryPort requisitoRepositoryPort;

    public CuadroDeObraService(CuadroDeObraRepositoryPort cuadroDeObraRepositoryPort, RequisitoLicitacionRepositoryPort requisitoRepositoryPort) {
        this.cuadroDeObraRepositoryPort = cuadroDeObraRepositoryPort;
        this.requisitoRepositoryPort = requisitoRepositoryPort;
    }

    @Override
    public RequisitoLicitacion saveRequisito(Long cuadroId, RequisitoLicitacion requisito) {
        CuadroDeObra cuadro = findCuadroById(cuadroId);
        requisito.setCuadroDeObra(cuadro);
        return requisitoRepositoryPort.save(requisito);
    }

    @Override
    @Transactional(readOnly = true)
    public RequisitoLicitacion getRequisitoByCuadroId(Long cuadroId) {
        return requisitoRepositoryPort.findByCuadroDeObraId(cuadroId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontraron requisitos para este proceso"));
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
    @Transactional
    public CuadroDeObra createCuadro (CuadroDeObra nuevoCuadro){
        nuevoCuadro.setCuadroDeObraEstado(CuadroDeObraEstado.POR_PRESENTAR);
        return cuadroDeObraRepositoryPort.save(nuevoCuadro);
    }

    @Override
    @Transactional
    public CuadroDeObra updateCuadro(Long id, CuadroDeObra cuadroConActualizaciones){
        CuadroDeObra cuadroExistente = findCuadroById(id);

        // fechaPublicacion es inmutable: una vez publicada por la entidad contratante en SECOP, no se modifica.
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
        aplicarCambioDeEstado(cuadroExistente, cuadroConActualizaciones.getCuadroDeObraEstado());

        return cuadroDeObraRepositoryPort.save(cuadroExistente);
    }

    @Override
    @Transactional
    public CuadroDeObra updateEstado(Long id, CuadroDeObraEstado nuevoEstado){
        CuadroDeObra cuadroExistente = findCuadroById(id);
        aplicarCambioDeEstado(cuadroExistente, nuevoEstado);
        return cuadroDeObraRepositoryPort.save(cuadroExistente);
    }

    private void aplicarCambioDeEstado(CuadroDeObra cuadro, CuadroDeObraEstado nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado == cuadro.getCuadroDeObraEstado()) {
            return;
        }
        if (!cuadro.getCuadroDeObraEstado().puedeTransicionarA(nuevoEstado)) {
            throw new IllegalArgumentException(
                    "Transición de estado inválida: " + cuadro.getCuadroDeObraEstado() + " → " + nuevoEstado);
        }
        cuadro.setCuadroDeObraEstado(nuevoEstado);
    }

    @Override
    @Transactional
    public void deleteCuadro(Long id){
        findCuadroById(id);
        requisitoRepositoryPort.deleteByCuadroDeObraId(id);
        cuadroDeObraRepositoryPort.delete(id);
    }

}