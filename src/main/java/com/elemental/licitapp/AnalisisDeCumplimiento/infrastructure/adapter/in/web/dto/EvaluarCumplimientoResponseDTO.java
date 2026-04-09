package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluarCumplimientoResponseDTO {
    private Long empresaId;
    private Long cuadroDeObraId;
    private String tipoParticipacion;
    private boolean cumpleGlobal;
    private List<DetalleRequisitoDTO> detalles;
    private List<SugerenciaConsorcioDTO> sugerencias;
}
