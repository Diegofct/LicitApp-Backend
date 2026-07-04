package com.elemental.licitapp.InteligenciaArtificial.domain.entity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.time.LocalDate;

/**
 * Informacion general del proponente extraida del RUP. Mapea 1:1 a los campos
 * homonimos de la entidad {@code Empresa}. Cualquier campo ausente en el documento
 * debe llegar como {@code null} (el modelo tiene prohibido inventar valores).
 */
public record InformacionGeneralRup(

        @JsonPropertyDescription("NIT del proponente tal como aparece en el RUP, incluyendo el digito de verificacion (ej. 245615365-1)")
        String nit,

        @JsonPropertyDescription("Razon social o nombre del proponente")
        String razonSocial,

        @JsonPropertyDescription("Numero de inscripcion del proponente en el registro")
        String numeroProponenteCcb,

        @JsonPropertyDescription("Tamano empresarial: micro, pequena, mediana o grande")
        String tamanoEmpresa,

        @JsonPropertyDescription("Nombre del representante legal")
        String representanteLegal,

        @JsonPropertyDescription("Numero de identificacion del representante legal")
        String identificacionRepresentanteLegal,

        @JsonPropertyDescription("Fecha de inscripcion en el RUP, en formato ISO yyyy-MM-dd")
        LocalDate fechaInscripcion,

        @JsonPropertyDescription("Fecha de ultima renovacion del RUP, en formato ISO yyyy-MM-dd")
        LocalDate fechaUltimaRenovacion,

        @JsonPropertyDescription("Direccion de contacto, si figura en el documento")
        String direccion,

        @JsonPropertyDescription("Telefono de contacto, si figura en el documento")
        String telefono,

        @JsonPropertyDescription("Correo electronico de contacto, si figura en el documento")
        String correo
) {
}
