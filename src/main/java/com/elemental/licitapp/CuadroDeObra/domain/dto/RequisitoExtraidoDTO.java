package com.elemental.licitapp.CuadroDeObra.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta estructurada de la IA al analizar un pliego de condiciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisitoExtraidoDTO {

    // --- EXPERIENCIA ---
    private String general;       
    private String especifica1;   
    private String especifica2;   
    private String secundaria;    
    private Integer contrato;     

    // --- FINANCIEROS ---
    private Double capitalTrabajo;     
    private Integer n;            
    private Double patrimonio;    
    
    // Indicadores financieros adicionales
    private Double liquidez;
    private Double endeudamiento;
    private Double razonCoberturaInteres;
    private Double rentabilidadPatrimonio;
    private Double rentabilidadActivo;

    // --- OTROS ---
    private Double kResidualProceso; 
    private Double poeAnticipo;      
}
