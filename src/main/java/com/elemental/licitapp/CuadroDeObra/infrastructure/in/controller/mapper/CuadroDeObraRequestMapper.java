package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.mapper;

import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.CuadroDeObraRequestDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.CuadroDeObraResponseDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.RequisitoLicitacionRequestDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.RequisitoLicitacionResponseDTO;

public final class CuadroDeObraRequestMapper {

    private CuadroDeObraRequestMapper() {}

    public static CuadroDeObra toEntity(CuadroDeObraRequestDTO dto) {
        if (dto == null) return null;
        CuadroDeObra c = new CuadroDeObra();
        c.setEntidadContratante(dto.getEntidadContratante());
        c.setNumeroProceso(dto.getNumeroProceso());
        c.setDescripcionObjeto(dto.getDescripcionObjeto());
        c.setEstadoProceso(dto.getEstadoProceso());
        c.setFechaPublicacion(dto.getFechaPublicacion());
        c.setFechaCierre(dto.getFechaCierre());
        c.setMonto(dto.getMonto());
        c.setValorSMMLV(dto.getValorSMMLV());
        c.setTipoProyecto(dto.getTipoProyecto());
        c.setDepartamento(dto.getDepartamento());
        c.setMunicipio(dto.getMunicipio());
        c.setExperiencia(dto.getExperiencia());
        c.setPlazo(dto.getPlazo());
        c.setAnticipo(dto.getAnticipo());
        c.setObservacion(dto.getObservacion());
        c.setCuadroDeObraEstado(dto.getCuadroDeObraEstado());
        return c;
    }

    public static RequisitoLicitacion toEntity(RequisitoLicitacionRequestDTO dto) {
        if (dto == null) return null;
        RequisitoLicitacion r = new RequisitoLicitacion();
        r.setGeneral(dto.getGeneral());
        r.setEspecifica1(dto.getEspecifica1());
        r.setEspecifica2(dto.getEspecifica2());
        r.setSecundaria(dto.getSecundaria());
        r.setContrato(dto.getContrato());
        r.setN(dto.getN());
        r.setPresupuesto(dto.getPresupuesto());
        r.setPatrimonio(dto.getPatrimonio());
        r.setCapitalTrabajo(dto.getCapitalTrabajo());
        r.setLiquidez(dto.getLiquidez());
        r.setEndeudamiento(dto.getEndeudamiento());
        r.setRazonCoberturaInteres(dto.getRazonCoberturaInteres());
        r.setRentabilidadPatrimonio(dto.getRentabilidadPatrimonio());
        r.setRentabilidadActivo(dto.getRentabilidadActivo());
        r.setKResidualProceso(dto.getKResidualProceso());
        r.setPoeAnticipo(dto.getPoeAnticipo());
        return r;
    }

    public static CuadroDeObraResponseDTO toResponseDTO(CuadroDeObra c) {
        if (c == null) return null;
        return CuadroDeObraResponseDTO.builder()
                .id(c.getId())
                .entidadContratante(c.getEntidadContratante())
                .numeroProceso(c.getNumeroProceso())
                .descripcionObjeto(c.getDescripcionObjeto())
                .estadoProceso(c.getEstadoProceso())
                .fechaPublicacion(c.getFechaPublicacion())
                .fechaCierre(c.getFechaCierre())
                .monto(c.getMonto())
                .valorSMMLV(c.getValorSMMLV())
                .tipoProyecto(c.getTipoProyecto())
                .departamento(c.getDepartamento())
                .municipio(c.getMunicipio())
                .experiencia(c.getExperiencia())
                .plazo(c.getPlazo())
                .anticipo(c.getAnticipo())
                .observacion(c.getObservacion())
                .cuadroDeObraEstado(c.getCuadroDeObraEstado())
                .build();
    }

    public static RequisitoLicitacionResponseDTO toResponseDTO(RequisitoLicitacion r) {
        if (r == null) return null;
        return RequisitoLicitacionResponseDTO.builder()
                .id(r.getId())
                .general(r.getGeneral())
                .especifica1(r.getEspecifica1())
                .especifica2(r.getEspecifica2())
                .secundaria(r.getSecundaria())
                .contrato(r.getContrato())
                .n(r.getN())
                .presupuesto(r.getPresupuesto())
                .patrimonio(r.getPatrimonio())
                .capitalTrabajo(r.getCapitalTrabajo())
                .liquidez(r.getLiquidez())
                .endeudamiento(r.getEndeudamiento())
                .razonCoberturaInteres(r.getRazonCoberturaInteres())
                .rentabilidadPatrimonio(r.getRentabilidadPatrimonio())
                .rentabilidadActivo(r.getRentabilidadActivo())
                .kResidualProceso(r.getKResidualProceso())
                .poeAnticipo(r.getPoeAnticipo())
                .build();
    }
}
