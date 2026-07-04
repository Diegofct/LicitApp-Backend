package com.elemental.licitapp.Empresa.infrastructure.in.controller.mapper;

import com.elemental.licitapp.Empresa.domain.entity.CapacidadResidualProponente;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Empresa.domain.entity.Experiencia;
import com.elemental.licitapp.Empresa.domain.entity.IndicadoresFinancieros;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.CapacidadResidualProponenteRequestDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.CapacidadResidualProponenteResponseDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.EmpresaRequestDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.EmpresaResponseDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.ExperienciaRequestDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.ExperienciaResponseDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.IndicadoresFinancierosRequestDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.IndicadoresFinancierosResponseDTO;

import java.util.List;

public final class EmpresaRequestMapper {

    private EmpresaRequestMapper() {}

    // ----- Request -> Entity -----

    public static Empresa toEntity(EmpresaRequestDTO dto) {
        if (dto == null) return null;
        Empresa e = Empresa.builder()
                .nit(dto.getNit())
                .razonSocial(dto.getRazonSocial())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .correo(dto.getCorreo())
                .numeroProponenteCcb(dto.getNumeroProponenteCcb())
                .tamanoEmpresa(dto.getTamanoEmpresa())
                .mipyme(dto.isMipyme())
                .proponenteMujer(dto.isProponenteMujer())
                .representanteLegal(dto.getRepresentanteLegal())
                .identificacionRepresentanteLegal(dto.getIdentificacionRepresentanteLegal())
                .fechaInscripcion(dto.getFechaInscripcion())
                .fechaUltimaRenovacion(dto.getFechaUltimaRenovacion())
                .build();

        if (dto.getIndicadores() != null) {
            e.setIndicadores(toEntity(dto.getIndicadores()));
        }
        if (dto.getExperiencias() != null) {
            List<Experiencia> exps = dto.getExperiencias().stream()
                    .map(EmpresaRequestMapper::toEntity)
                    .toList();
            e.getExperiencias().addAll(exps);
        }
        if (dto.getCapacidadResidual() != null) {
            CapacidadResidualProponente cap = toEntity(dto.getCapacidadResidual());
            e.getCapacidadesResiduales().add(cap);
        }
        return e;
    }

    public static IndicadoresFinancieros toEntity(IndicadoresFinancierosRequestDTO dto) {
        if (dto == null) return null;
        IndicadoresFinancieros i = new IndicadoresFinancieros();
        i.setAnioCierre(dto.getAnioCierre());
        i.setActivoCorriente(dto.getActivoCorriente());
        i.setPasivoCorriente(dto.getPasivoCorriente());
        i.setActivoTotal(dto.getActivoTotal());
        i.setPasivoTotal(dto.getPasivoTotal());
        i.setUtilidadOperacional(dto.getUtilidadOperacional());
        i.setGastosInteres(dto.getGastosInteres());
        return i;
    }

    public static Experiencia toEntity(ExperienciaRequestDTO dto) {
        if (dto == null) return null;
        return Experiencia.builder()
                .contratista(dto.getContratista())
                .entidadContratante(dto.getEntidadContratante())
                .valorSMMLV(dto.getValorSMMLV())
                .porcentajeParticipacion(dto.getPorcentajeParticipacion())
                .codigosUNSPSC(dto.getCodigosUNSPSC())
                .build();
    }

    public static CapacidadResidualProponente toEntity(CapacidadResidualProponenteRequestDTO dto) {
        if (dto == null) return null;
        return CapacidadResidualProponente.builder()
                .id(dto.getId())
                .capacidadOrganizacion(dto.getCapacidadOrganizacion())
                .experiencia(dto.getExperiencia())
                .capacidadTecnica(dto.getCapacidadTecnica())
                .capacidadFinanciera(dto.getCapacidadFinanciera())
                .saldosContratosEjecucion(dto.getSaldosContratosEjecucion())
                .build();
    }

    // ----- Entity -> Response -----

    public static EmpresaResponseDTO toResponse(Empresa e) {
        if (e == null) return null;
        return EmpresaResponseDTO.builder()
                .id(e.getId())
                .nit(e.getNit())
                .razonSocial(e.getRazonSocial())
                .direccion(e.getDireccion())
                .telefono(e.getTelefono())
                .correo(e.getCorreo())
                .numeroProponenteCcb(e.getNumeroProponenteCcb())
                .tamanoEmpresa(e.getTamanoEmpresa())
                .mipyme(e.isMipyme())
                .proponenteMujer(e.isProponenteMujer())
                .representanteLegal(e.getRepresentanteLegal())
                .identificacionRepresentanteLegal(e.getIdentificacionRepresentanteLegal())
                .fechaInscripcion(e.getFechaInscripcion())
                .fechaUltimaRenovacion(e.getFechaUltimaRenovacion())
                .indicadores(toResponse(e.getIndicadores()))
                .experiencias(e.getExperiencias() == null ? List.of()
                        : e.getExperiencias().stream().map(EmpresaRequestMapper::toResponse).toList())
                .capacidadResidual(e.getCapacidadesResiduales() == null ? null
                        : e.getCapacidadesResiduales().stream()
                                .findFirst()
                                .map(EmpresaRequestMapper::toResponse)
                                .orElse(null))
                .build();
    }

    public static IndicadoresFinancierosResponseDTO toResponse(IndicadoresFinancieros i) {
        if (i == null) return null;
        return IndicadoresFinancierosResponseDTO.builder()
                .id(i.getId())
                .anioCierre(i.getAnioCierre())
                .activoCorriente(i.getActivoCorriente())
                .pasivoCorriente(i.getPasivoCorriente())
                .activoTotal(i.getActivoTotal())
                .pasivoTotal(i.getPasivoTotal())
                .utilidadOperacional(i.getUtilidadOperacional())
                .gastosInteres(i.getGastosInteres())
                .liquidez(i.getLiquidez())
                .estadoLiquidez(i.getEstadoLiquidez())
                .endeudamiento(i.getEndeudamiento())
                .estadoEndeudamiento(i.getEstadoEndeudamiento())
                .razonCoberturaInteres(i.getRazonCoberturaInteres())
                .estadoRazonCoberturaInteres(i.getEstadoRazonCoberturaInteres())
                .patrimonio(i.getPatrimonio())
                .capitalTrabajo(i.getCapitalTrabajo())
                .rentabilidadPatrimonio(i.getRentabilidadPatrimonio())
                .estadoRentabilidadPatrimonio(i.getEstadoRentabilidadPatrimonio())
                .rentabilidadActivo(i.getRentabilidadActivo())
                .estadoRentabilidadActivo(i.getEstadoRentabilidadActivo())
                .build();
    }

    public static ExperienciaResponseDTO toResponse(Experiencia e) {
        if (e == null) return null;
        return ExperienciaResponseDTO.builder()
                .id(e.getId())
                .contratista(e.getContratista())
                .entidadContratante(e.getEntidadContratante())
                .valorSMMLV(e.getValorSMMLV())
                .porcentajeParticipacion(e.getPorcentajeParticipacion())
                .codigosUNSPSC(e.getCodigosUNSPSC())
                .build();
    }

    public static CapacidadResidualProponenteResponseDTO toResponse(CapacidadResidualProponente c) {
        if (c == null) return null;
        return CapacidadResidualProponenteResponseDTO.builder()
                .id(c.getId())
                .capacidadOrganizacion(c.getCapacidadOrganizacion())
                .experiencia(c.getExperiencia())
                .capacidadTecnica(c.getCapacidadTecnica())
                .capacidadFinanciera(c.getCapacidadFinanciera())
                .saldosContratosEjecucion(c.getSaldosContratosEjecucion())
                .resultadoCapacidadResidualProponente(c.getResultadoK())
                .build();
    }
}
