package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

import com.elemental.licitapp.CuadroDeObra.domain.enums.PresentacionMarca;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Body de PATCH /cuadro-de-obra/{id}/presentacion. {@code presentacion} es opcional: un
 * valor null limpia la marca (vuelve a "sin marca"), completando el ciclo SI -> NO -> null.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPresentacionRequestDTO {

    private PresentacionMarca presentacion;
}
