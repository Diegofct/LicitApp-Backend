package com.elemental.licitapp.CuadroDeObra.application.service;

import com.elemental.licitapp.CuadroDeObra.application.ports.out.CuadroDeObraRepositoryPort;
import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CuadroDeObraService {

    private final CuadroDeObraRepositoryPort cuadroDeObraRepositoryPort;

    public CuadroDeObraService(CuadroDeObraRepositoryPort cuadroDeObraRepositoryPort) {
        this.cuadroDeObraRepositoryPort = cuadroDeObraRepositoryPort;
    }

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

    @Transactional(readOnly = true)
    public CuadroDeObra findCuadroById(Long id) {
        return cuadroDeObraRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuadro de obra con id: " + id + "no encontrado"));
    }

    @Transactional
    public CuadroDeObra createCuadro (CuadroDeObra nuevoCuadro){
        nuevoCuadro.setCuadroDeObraEstado(CuadroDeObraEstado.POR_PRESENTAR);
        return cuadroDeObraRepositoryPort.save(nuevoCuadro);
    }

    @Transactional
    public CuadroDeObra updateCuadro(Long id, CuadroDeObra cuadroConActualizaciones){
        CuadroDeObra cuadroExistente = findCuadroById(id);

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
        cuadroExistente.setCuadroDeObraEstado(cuadroConActualizaciones.getCuadroDeObraEstado());

        return cuadroDeObraRepositoryPort.save(cuadroExistente);
    }

    @Transactional
    public CuadroDeObra updateEstado(Long id, CuadroDeObraEstado nuevoEstado){
        CuadroDeObra cuadroExistente = findCuadroById(id);
        cuadroExistente.setCuadroDeObraEstado(nuevoEstado);
        return cuadroDeObraRepositoryPort.save(cuadroExistente);
    }

    public void deleteCuadro(Long id){
        findCuadroById(id);
        cuadroDeObraRepositoryPort.delete(id);
    }

}