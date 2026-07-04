package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class EmpresaRequestDTO {

    @NotBlank(message = "nit es obligatorio")
    private String nit;

    @NotBlank(message = "razonSocial es obligatorio")
    private String razonSocial;

    private String direccion;
    private String telefono;

    @Email(message = "correo no tiene formato valido")
    private String correo;

    private String numeroProponenteCcb;
    private String tamanoEmpresa;

    // Flags de negocio: MiPyme (tamaño) y proponente mujer (condición). Separados del
    // texto libre tamanoEmpresa, que se conserva solo como dato descriptivo.
    private boolean mipyme;
    private boolean proponenteMujer;

    private String representanteLegal;
    private String identificacionRepresentanteLegal;

    private LocalDate fechaInscripcion;
    private LocalDate fechaUltimaRenovacion;

    @Valid
    private IndicadoresFinancierosRequestDTO indicadores;

    @Valid
    private List<ExperienciaRequestDTO> experiencias;

    @Valid
    private CapacidadResidualProponenteRequestDTO capacidadResidual;
}
