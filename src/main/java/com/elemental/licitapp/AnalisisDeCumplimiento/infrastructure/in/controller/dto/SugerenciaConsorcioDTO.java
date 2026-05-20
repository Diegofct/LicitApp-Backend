package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SugerenciaConsorcioDTO {
    private Long empresaId;
    private String nit;
    private String razonSocial;
}
