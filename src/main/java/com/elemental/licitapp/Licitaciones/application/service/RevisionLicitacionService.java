package com.elemental.licitapp.Licitaciones.application.service;

import com.elemental.licitapp.Licitaciones.application.ports.in.RevisionLicitacionUseCase;
import com.elemental.licitapp.Licitaciones.application.ports.out.RevisionLicitacionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RevisionLicitacionService implements RevisionLicitacionUseCase {

    private final RevisionLicitacionRepositoryPort repositoryPort;

    public RevisionLicitacionService(RevisionLicitacionRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerRevisiones() {
        return repositoryPort.obtenerIdsRevisados();
    }

    @Override
    @Transactional
    public void marcarRevisada(String idDelProceso) {
        // Idempotente: solo inserta si aun no existe, para no pisar la auditoria original.
        if (idDelProceso == null || idDelProceso.isBlank() || repositoryPort.existe(idDelProceso)) {
            return;
        }
        repositoryPort.marcar(idDelProceso);
    }

    @Override
    @Transactional
    public void desmarcarRevisada(String idDelProceso) {
        // Idempotente: borrar algo que no existe no es un error.
        if (idDelProceso == null || idDelProceso.isBlank() || !repositoryPort.existe(idDelProceso)) {
            return;
        }
        repositoryPort.desmarcar(idDelProceso);
    }
}
