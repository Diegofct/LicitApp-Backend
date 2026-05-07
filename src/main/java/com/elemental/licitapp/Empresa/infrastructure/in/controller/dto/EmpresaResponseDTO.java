package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaResponseDTO {

    private Long id;
    private String nit;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String correo;
    private String numeroProponenteCcb;
    private String tamanoEmpresa;
    private String representanteLegal;
    private String identificacionRepresentanteLegal;
    private LocalDate fechaInscripcion;
    private LocalDate fechaUltimaRenovacion;

    private IndicadoresFinancierosResponseDTO indicadores;
    private List<ExperienciaResponseDTO> experiencias;
    private CapacidadResidualProponenteResponseDTO capacidadResidual;
}
