package com.elemental.licitapp.Empresa.infrastructure.in.controller.mapper;

import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.ExperienciaRequestDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.ExtraccionRupResponseDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.IndicadoresFinancierosRequestDTO;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.CierreFiscalExtraido;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.DatosRupExtraidos;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ExperienciaExtraida;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.InformacionGeneralRup;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccion;

import java.util.List;

/**
 * Mapea el resultado de extraccion del slice IA al borrador que consume el front,
 * reutilizando los DTO de request existentes (estilo {@code EmpresaRequestMapper},
 * metodos estaticos, sin MapStruct).
 */
public final class ExtraccionRupResponseMapper {

    private ExtraccionRupResponseMapper() {}

    public static ExtraccionRupResponseDTO toResponse(ResultadoExtraccion resultado) {
        DatosRupExtraidos datos = resultado.datos();
        InformacionGeneralRup g = datos.informacionGeneral();

        return ExtraccionRupResponseDTO.builder()
                .nit(g.nit())
                .razonSocial(g.razonSocial())
                .direccion(g.direccion())
                .telefono(g.telefono())
                .correo(g.correo())
                .numeroProponenteCcb(g.numeroProponenteCcb())
                .tamanoEmpresa(g.tamanoEmpresa())
                .representanteLegal(g.representanteLegal())
                .identificacionRepresentanteLegal(g.identificacionRepresentanteLegal())
                .fechaInscripcion(g.fechaInscripcion())
                .fechaUltimaRenovacion(g.fechaUltimaRenovacion())
                .cierresFiscales(toIndicadores(datos.cierresFiscales()))
                .experiencias(toExperiencias(datos.experiencias()))
                .advertencias(resultado.advertencias())
                .build();
    }

    private static List<IndicadoresFinancierosRequestDTO> toIndicadores(List<CierreFiscalExtraido> cierres) {
        if (cierres == null) {
            return List.of();
        }
        return cierres.stream()
                .map(c -> IndicadoresFinancierosRequestDTO.builder()
                        .anioCierre(c.anioCierre())
                        .activoCorriente(c.activoCorriente())
                        .pasivoCorriente(c.pasivoCorriente())
                        .activoTotal(c.activoTotal())
                        .pasivoTotal(c.pasivoTotal())
                        .utilidadOperacional(c.utilidadOperacional())
                        .gastosInteres(c.gastosInteres())
                        .build())
                .toList();
    }

    private static List<ExperienciaRequestDTO> toExperiencias(List<ExperienciaExtraida> experiencias) {
        if (experiencias == null) {
            return List.of();
        }
        return experiencias.stream()
                .map(e -> ExperienciaRequestDTO.builder()
                        .contratista(e.contratista())
                        .entidadContratante(e.entidadContratante())
                        .valorSMMLV(e.valorSMMLV())
                        .porcentajeParticipacion(e.porcentajeParticipacion())
                        .codigosUNSPSC(e.codigosUNSPSC())
                        .build())
                .toList();
    }
}
