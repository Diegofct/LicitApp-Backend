package com.elemental.licitapp.Sobre2.infrastructure.out.repository;

import com.elemental.licitapp.Sobre2.application.ports.out.OferenteProcesoRepositoryPort;
import com.elemental.licitapp.Sobre2.domain.entity.OferenteProceso;
import com.elemental.licitapp.Sobre2.domain.entity.ResumenCompetidor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class OferenteProcesoRepositoryAdapter implements OferenteProcesoRepositoryPort {

    private final OferenteProcesoJpaRepository repository;

    public OferenteProcesoRepositoryAdapter(OferenteProcesoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<OferenteProceso> findByCuadroId(Long cuadroId) {
        return repository.findByCuadroDeObraIdOrderByValorOfertaAsc(cuadroId);
    }

    @Override
    public Optional<OferenteProceso> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<OferenteProceso> findByCuadroIdEIdentificador(Long cuadroId, String identificadorOferta) {
        if (identificadorOferta == null) {
            return Optional.empty();
        }
        return repository.findByCuadroDeObraIdAndIdentificadorOferta(cuadroId, identificadorOferta);
    }

    @Override
    public OferenteProceso save(OferenteProceso oferente) {
        return repository.save(oferente);
    }

    @Override
    public List<OferenteProceso> saveAll(List<OferenteProceso> oferentes) {
        return repository.saveAll(oferentes);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAllById(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        repository.deleteAllById(ids);
    }

    @Override
    public List<ResumenCompetidor> resumenPorCompetidor(String nombre) {
        String filtro = (nombre == null || nombre.isBlank()) ? null : nombre.trim();
        return repository.agregarPorCompetidor(filtro).stream()
                .map(OferenteProcesoRepositoryAdapter::toResumen)
                .toList();
    }

    private static ResumenCompetidor toResumen(Object[] fila) {
        return new ResumenCompetidor(
                (String) fila[0],
                ((Number) fila[1]).longValue(),
                aDecimal(fila[2]),
                aDecimal(fila[3]),
                aDecimal(fila[4]));
    }

    /** AVG en JPQL puede volver como Double segun el dialecto; se normaliza la escala. */
    private static BigDecimal aDecimal(Object valor) {
        if (valor == null) {
            return null;
        }
        BigDecimal decimal = valor instanceof BigDecimal bd
                ? bd
                : BigDecimal.valueOf(((Number) valor).doubleValue());
        return decimal.setScale(2, RoundingMode.HALF_UP);
    }
}
