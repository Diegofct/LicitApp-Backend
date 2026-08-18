package com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/** Alta o edicion manual de un oferente. La validacion vive en el adaptador de entrada. */
@Data
public class OferenteProcesoRequestDTO {

    @NotBlank(message = "El nombre del oferente es obligatorio")
    @Size(max = 500, message = "El nombre del oferente no puede superar 500 caracteres")
    private String nombreOferente;

    /** Opcional: los consorcios y uniones temporales no tienen NIT propio publicado. */
    @Size(max = 32, message = "El NIT no puede superar 32 caracteres")
    private String nitOferente;

    @NotNull(message = "El valor de la oferta es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor de la oferta debe ser mayor que cero")
    private BigDecimal valorOferta;

    @Size(max = 16, message = "La moneda no puede superar 16 caracteres")
    private String moneda;

    /** Falso para excluir la oferta de las formulas (p. ej. rechazada por la entidad). */
    private Boolean valida;
}
