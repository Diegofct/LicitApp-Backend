package com.elemental.licitapp.Licitaciones.application.service;

import com.elemental.licitapp.Licitaciones.application.ports.out.SecopApiPort;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LicitacionesService {

    private final SecopApiPort secopApiPort;

    public LicitacionesService(SecopApiPort secopApiPort) {
        this.secopApiPort = secopApiPort;
    }

    public List<Licitacion> obtenerProcesosActivos(int pageNumber, int pageSize) {
        List<Licitacion> licitaciones = secopApiPort.obtenerProcesosActivos(pageNumber, pageSize);
        return licitaciones.stream()
                .filter(licitacion -> licitacion.getFechaPublicacion() != null)
                .filter(licitacion -> licitacion.getUrlSecop() != null && !licitacion.getUrlSecop().contains("STS/Users/Login"))
                .toList();
    }
}
