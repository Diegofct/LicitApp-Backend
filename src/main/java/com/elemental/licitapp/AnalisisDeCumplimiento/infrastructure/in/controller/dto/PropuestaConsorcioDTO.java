package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropuestaConsorcioDTO {
    private List<IntegrantePropuestoDTO> integrantes;
    private boolean cumpleGlobal;
    private List<String> requisitosCubiertos;
    private List<String> requisitosPendientes;
}
