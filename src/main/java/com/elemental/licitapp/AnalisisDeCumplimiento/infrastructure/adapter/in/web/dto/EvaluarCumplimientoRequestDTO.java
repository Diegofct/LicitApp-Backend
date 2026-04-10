package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluarCumplimientoRequestDTO {
    private Long empresaId;
    private Long cuadroDeObraId;
}
