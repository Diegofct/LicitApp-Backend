package com.elemental.licitapp.SeguimientoProceso.application.service;

import com.elemental.licitapp.Exception.ResourceNotFoundException;
import com.elemental.licitapp.SeguimientoProceso.application.ports.in.ConsultarSeguimientoUseCase;
import com.elemental.licitapp.SeguimientoProceso.application.ports.in.InicializarSeguimientoUseCase;
import com.elemental.licitapp.SeguimientoProceso.application.ports.in.RegistrarEventoUseCase;
import com.elemental.licitapp.SeguimientoProceso.application.ports.out.SeguimientoRepositoryPort;
import com.elemental.licitapp.SeguimientoProceso.domain.entity.EventoSeguimiento;
import com.elemental.licitapp.SeguimientoProceso.domain.entity.SeguimientoProceso;
import com.elemental.licitapp.SeguimientoProceso.domain.enums.TipoEventoSeguimiento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SeguimientoService
        implements InicializarSeguimientoUseCase, RegistrarEventoUseCase, ConsultarSeguimientoUseCase {

    private final SeguimientoRepositoryPort seguimientoRepository;

    public SeguimientoService(SeguimientoRepositoryPort seguimientoRepository) {
        this.seguimientoRepository = seguimientoRepository;
    }

    @Override
    @Transactional
    public SeguimientoProceso inicializar(Long cuadroDeObraId) {
        if (cuadroDeObraId == null) {
            throw new IllegalArgumentException("cuadroDeObraId es obligatorio.");
        }

        Optional<SeguimientoProceso> existente = seguimientoRepository.buscarPorCuadroDeObra(cuadroDeObraId);
        if (existente.isPresent()) {
            return existente.get();
        }

        LocalDateTime ahora = LocalDateTime.now();
        SeguimientoProceso nuevo = SeguimientoProceso.builder()
                .cuadroDeObraId(cuadroDeObraId)
                .fechaInicio(ahora)
                .build();

        EventoSeguimiento eventoInicial = EventoSeguimiento.builder()
                .seguimiento(nuevo)
                .tipo(TipoEventoSeguimiento.OFERTA_PRESENTADA)
                .fechaEvento(ahora)
                .descripcion("Oferta presentada exitosamente.")
                .fechaRegistro(ahora)
                .build();
        nuevo.getEventos().add(eventoInicial);

        return seguimientoRepository.guardar(nuevo);
    }

    @Override
    @Transactional
    public EventoSeguimiento registrar(Long cuadroDeObraId, EventoSeguimiento evento) {
        validar(evento);

        SeguimientoProceso seguimiento = seguimientoRepository.buscarPorCuadroDeObra(cuadroDeObraId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe seguimiento para el cuadro " + cuadroDeObraId
                                + ". El proceso debe estar en estado PRESENTADO."));

        evento.setSeguimiento(seguimiento);
        evento.setFechaRegistro(LocalDateTime.now());
        seguimiento.getEventos().add(evento);

        SeguimientoProceso guardado = seguimientoRepository.guardar(seguimiento);
        return guardado.getEventos().stream()
                .filter(e -> e == evento || (e.getId() != null && e.getId().equals(evento.getId())))
                .findFirst()
                .orElse(evento);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SeguimientoProceso> obtenerPorCuadroDeObra(Long cuadroDeObraId) {
        return seguimientoRepository.buscarPorCuadroDeObra(cuadroDeObraId);
    }

    private void validar(EventoSeguimiento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento es obligatorio.");
        }
        if (evento.getTipo() == null) {
            throw new IllegalArgumentException("El tipo del evento es obligatorio.");
        }
        if (evento.getFechaEvento() == null) {
            throw new IllegalArgumentException("La fechaEvento es obligatoria.");
        }
        if (evento.getTipo() == TipoEventoSeguimiento.OTRO
                && (evento.getDescripcion() == null || evento.getDescripcion().isBlank())) {
            throw new IllegalArgumentException(
                    "Cuando el tipo es OTRO, la descripción es obligatoria.");
        }
    }
}
