package com.elemental.licitapp.Licitaciones.application.service;

import com.elemental.licitapp.Licitaciones.application.ports.out.SecopApiPort;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class LicitacionesService {

    private final SecopApiPort secopApiPort;

    public LicitacionesService(SecopApiPort secopApiPort) {
        this.secopApiPort = secopApiPort;
    }

    public Page<Licitacion> obtenerLicitacionesPublicas(Pageable pageable) {
        Page<Licitacion> licitacionesPage = secopApiPort.obtenerLicitacionesPorModalidad("Licitación pública", pageable);
        List<Licitacion> filteredLicitaciones = filtrarYProcesarLicitaciones(licitacionesPage.getContent());
        return new PageImpl<>(filteredLicitaciones, pageable, licitacionesPage.getTotalElements());
    }

    public Page<Licitacion> obtenerLicitacionesObraPublica(Pageable pageable) {
        Page<Licitacion> licitacionesPage = secopApiPort.obtenerLicitacionesPorModalidad("Licitación pública Obra Publica", pageable);
        List<Licitacion> filteredLicitaciones = filtrarYProcesarLicitaciones(licitacionesPage.getContent());
        return new PageImpl<>(filteredLicitaciones, pageable, licitacionesPage.getTotalElements());
    }

    private List<Licitacion> filtrarYProcesarLicitaciones(List<Licitacion> licitaciones) {
        return licitaciones.stream()
                .filter(licitacion -> licitacion.getFechaPublicacion() != null)
                .filter(licitacion -> licitacion.getUrlSecop() != null && !licitacion.getUrlSecop().contains("STS/Users/Login"))
                .toList();
    }
}
