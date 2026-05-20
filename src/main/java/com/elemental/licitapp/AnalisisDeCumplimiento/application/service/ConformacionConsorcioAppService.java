package com.elemental.licitapp.AnalisisDeCumplimiento.application.service;

import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.ConsultarConsorcioUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.GuardarConformacionConsorcioUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ConformacionConsorcioRepositoryPort;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ObtenerEmpresasPort;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ConformacionConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.IntegranteConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ConformacionConsorcioAppService
        implements GuardarConformacionConsorcioUseCase, ConsultarConsorcioUseCase {

    private static final BigDecimal CIEN = new BigDecimal("100.00");
    private static final BigDecimal TOLERANCIA_SUMA = new BigDecimal("0.01");

    private final ConformacionConsorcioRepositoryPort conformacionRepository;
    private final ObtenerEmpresasPort obtenerEmpresasPort;

    public ConformacionConsorcioAppService(ConformacionConsorcioRepositoryPort conformacionRepository,
                                           ObtenerEmpresasPort obtenerEmpresasPort) {
        this.conformacionRepository = conformacionRepository;
        this.obtenerEmpresasPort = obtenerEmpresasPort;
    }

    @Override
    @Transactional
    public ConformacionConsorcio guardar(ConformacionConsorcio conformacion) {
        validar(conformacion);

        if (conformacion.getFechaConformacion() == null) {
            conformacion.setFechaConformacion(LocalDateTime.now());
        }

        // Si ya existe conformacion para el cuadro, se reemplaza (UPSERT logico).
        Optional<ConformacionConsorcio> existente =
                conformacionRepository.buscarPorCuadroDeObra(conformacion.getCuadroDeObraId());
        existente.ifPresent(prev -> conformacion.setId(prev.getId()));

        conformacion.getIntegrantes().forEach(i -> i.setConformacion(conformacion));
        return conformacionRepository.guardar(conformacion);
    }

    @Override
    public Optional<ConformacionConsorcio> buscarPorCuadroDeObra(Long cuadroDeObraId) {
        return conformacionRepository.buscarPorCuadroDeObra(cuadroDeObraId);
    }

    private void validar(ConformacionConsorcio c) {
        if (c == null) {
            throw new IllegalArgumentException("La conformación es obligatoria.");
        }
        if (c.getCuadroDeObraId() == null) {
            throw new IllegalArgumentException("El cuadroDeObraId es obligatorio.");
        }
        if (c.getTipoParticipacion() == null) {
            throw new IllegalArgumentException("El tipo de participación es obligatorio.");
        }

        List<IntegranteConsorcio> integrantes = c.getIntegrantes();
        if (integrantes == null || integrantes.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos un integrante.");
        }

        validarCardinalidadSegunTipo(c.getTipoParticipacion(), integrantes);
        validarEmpresasUnicas(integrantes);
        validarPorcentajes(integrantes);
        validarEmpresasExisten(integrantes);
    }

    private void validarCardinalidadSegunTipo(TipoParticipacion tipo, List<IntegranteConsorcio> integrantes) {
        switch (tipo) {
            case INDIVIDUAL -> {
                if (integrantes.size() != 1) {
                    throw new IllegalArgumentException(
                            "Participación INDIVIDUAL debe tener exactamente 1 integrante.");
                }
                BigDecimal pct = integrantes.get(0).getPorcentajeParticipacion();
                if (pct == null || pct.compareTo(CIEN) != 0) {
                    throw new IllegalArgumentException(
                            "Participación INDIVIDUAL debe tener el integrante al 100%.");
                }
            }
            case CONSORCIO, UNION_TEMPORAL -> {
                if (integrantes.size() < 2) {
                    throw new IllegalArgumentException(
                            "Participación " + tipo + " debe tener al menos 2 integrantes.");
                }
            }
        }
    }

    private void validarEmpresasUnicas(List<IntegranteConsorcio> integrantes) {
        Set<Long> vistas = new HashSet<>();
        for (IntegranteConsorcio i : integrantes) {
            if (i.getEmpresaId() == null) {
                throw new IllegalArgumentException("Todo integrante debe tener empresaId.");
            }
            if (!vistas.add(i.getEmpresaId())) {
                throw new IllegalArgumentException(
                        "La empresa " + i.getEmpresaId() + " aparece más de una vez en la conformación.");
            }
        }
    }

    private void validarPorcentajes(List<IntegranteConsorcio> integrantes) {
        BigDecimal suma = BigDecimal.ZERO;
        for (IntegranteConsorcio i : integrantes) {
            BigDecimal pct = i.getPorcentajeParticipacion();
            if (pct == null) {
                throw new IllegalArgumentException(
                        "El integrante (empresa " + i.getEmpresaId() + ") no tiene porcentaje.");
            }
            if (pct.compareTo(BigDecimal.ZERO) <= 0 || pct.compareTo(CIEN) > 0) {
                throw new IllegalArgumentException(
                        "El porcentaje del integrante (empresa " + i.getEmpresaId()
                                + ") debe estar entre 0 (exclusivo) y 100 (inclusive).");
            }
            suma = suma.add(pct);
        }
        BigDecimal diferencia = suma.subtract(CIEN).abs().setScale(2, RoundingMode.HALF_UP);
        if (diferencia.compareTo(TOLERANCIA_SUMA) > 0) {
            throw new IllegalArgumentException(
                    "La suma de porcentajes debe ser 100 (actual: " + suma + ").");
        }
    }

    private void validarEmpresasExisten(List<IntegranteConsorcio> integrantes) {
        for (IntegranteConsorcio i : integrantes) {
            obtenerEmpresasPort.obtenerPorId(i.getEmpresaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Empresa no encontrada con ID: " + i.getEmpresaId()));
        }
    }
}
