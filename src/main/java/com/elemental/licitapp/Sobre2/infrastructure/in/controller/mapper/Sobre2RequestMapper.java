package com.elemental.licitapp.Sobre2.infrastructure.in.controller.mapper;

import com.elemental.licitapp.Sobre2.domain.entity.OferenteProceso;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.OferenteProcesoRequestDTO;

/** Clase utilitaria con metodos estaticos, como el resto de mappers del proyecto. */
public final class Sobre2RequestMapper {

    private Sobre2RequestMapper() {
    }

    public static OferenteProceso toEntity(OferenteProcesoRequestDTO dto) {
        OferenteProceso oferente = new OferenteProceso();
        oferente.setNombreOferente(dto.getNombreOferente() != null ? dto.getNombreOferente().trim() : null);
        oferente.setNitOferente(dto.getNitOferente());
        oferente.setValorOferta(dto.getValorOferta());
        oferente.setMoneda(dto.getMoneda());
        oferente.setValida(dto.getValida());
        return oferente;
    }
}
