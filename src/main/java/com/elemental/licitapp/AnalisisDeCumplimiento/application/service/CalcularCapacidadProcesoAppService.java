package com.elemental.licitapp.AnalisisDeCumplimiento.application.service;

import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.CalcularCapacidadProcesoUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas.ReglaCapacidadResidual;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CalcularCapacidadProcesoAppService implements CalcularCapacidadProcesoUseCase {

    @Override
    public BigDecimal calcularCRPC(BigDecimal presupuestoOficial, BigDecimal anticipo) {
        return ReglaCapacidadResidual.calcularCRPC(presupuestoOficial, anticipo);
    }

    @Override
    public BigDecimal calcularCTd(BigDecimal presupuestoOficial, BigDecimal anticipo) {
        return ReglaCapacidadResidual.calcularCTd(presupuestoOficial, anticipo);
    }
}
