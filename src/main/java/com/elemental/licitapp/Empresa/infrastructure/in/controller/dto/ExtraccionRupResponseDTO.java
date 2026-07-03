package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Borrador editable que devuelve POST /empresas/extraer-rup. El front lo muestra, el
 * analista lo revisa/corrige y, al confirmar, arma un {@link EmpresaRequestDTO} (eligiendo
 * cual cierre fiscal guardar) y usa los endpoints existentes de creacion/actualizacion.
 * Reutiliza los DTO de request para que el mapeo al confirmar sea 1:1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtraccionRupResponseDTO {

    // Informacion general
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

    /** Todos los cierres fiscales hallados; el analista elige cual guardar. */
    private List<IndicadoresFinancierosRequestDTO> cierresFiscales;

    private List<ExperienciaRequestDTO> experiencias;

    /** Avisos de la validacion (campos nulos, descuadres de verificacion cruzada, etc.). */
    private List<String> advertencias;
}
