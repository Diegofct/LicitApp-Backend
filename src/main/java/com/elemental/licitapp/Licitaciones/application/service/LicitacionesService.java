package com.elemental.licitapp.Licitaciones.application.service;

import com.elemental.licitapp.Licitaciones.application.ports.out.SecopApiPort;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class LicitacionesService {

    private final SecopApiPort secopApiPort;

    public LicitacionesService(SecopApiPort secopApiPort) {
        this.secopApiPort = secopApiPort;
    }

    public List<Licitacion> obtenerLicitacionesPublicas(int pageNumber, int pageSize) {
        List<Licitacion> licitaciones = secopApiPort.obtenerLicitacionesPorModalidad("Licitación pública", pageNumber, pageSize);
        return filtrarYProcesarLicitaciones(licitaciones);
    }

    public List<Licitacion> obtenerLicitacionesObraPublica(int pageNumber, int pageSize) {
        List<Licitacion> licitaciones = secopApiPort.obtenerLicitacionesPorModalidad("Licitación pública Obra Publica", pageNumber, pageSize);
        return filtrarYProcesarLicitaciones(licitaciones);
    }

    private List<Licitacion> filtrarYProcesarLicitaciones(List<Licitacion> licitaciones) {
        return licitaciones.stream()
                .filter(licitacion -> licitacion.getFechaPublicacion() != null)
                .filter(licitacion -> licitacion.getUrlSecop() != null && !licitacion.getUrlSecop().contains("STS/Users/Login"))
                .toList();
    }
}
