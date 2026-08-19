package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.mapper;

import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto.SecopLicitacionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class SecopLicitacionMapper {

    /** Días por mes usados para llevar a meses las duraciones expresadas en días. */
    private static final double DIAS_POR_MES = 30.0;

    public Licitacion toEntity(SecopLicitacionDTO dto) {
        if (dto == null) {
            return null;
        }

        Licitacion licitacion = new Licitacion();
        licitacion.setIdDelProceso(dto.getIdDelProceso());
        licitacion.setEntidad(dto.getEntidad());
        licitacion.setObjeto(dto.getObjeto());
        licitacion.setCuantia(dto.getCuantia());
        licitacion.setModalidad(dto.getModalidad());
        licitacion.setNumero(dto.getNumero());
        licitacion.setEstado(dto.getEstado());
        licitacion.setCodigoUnpspc(dto.getCodigoUnpspc());
        licitacion.setFechaPublicacion(dto.getFechaPublicacionConsolidada());
        licitacion.setUbicacion(buildUbicacion(dto));
        licitacion.setUrlSecop(extractUrl(dto.getUrlProceso()));
        licitacion.setFechaCierre(dto.getFechaRecepcionOfertas());
        licitacion.setPlazoMeses(normalizarPlazoMeses(dto.getDuracion(), dto.getUnidadDuracion()));
        licitacion.setIdDelPortafolio(dto.getIdDelPortafolio());
        return licitacion;
    }

    private String buildUbicacion(SecopLicitacionDTO dto) {
        String depto = dto.getDepartamentoEntidad() != null ? dto.getDepartamentoEntidad() : "";
        String ciudad = dto.getCiudadEntidad() != null ? dto.getCiudadEntidad() : "";
        return depto + " - " + ciudad;
    }

    /**
     * Lleva a meses el par {@code duracion} + {@code unidad_de_duracion} del dataset, que el
     * Cuadro de Obra maneja siempre en meses.
     *
     * <p>Se devuelve {@code null} —y no un valor aproximado— cuando el dato no es utilizable,
     * para que el analista lo digite en vez de confiar en una conversión dudosa. Es un caso
     * frecuente: cerca del 15% de las licitaciones de obra publican {@code duracion = "0"}, y
     * unas pocas la expresan en horas.
     */
    private Integer normalizarPlazoMeses(String duracion, String unidad) {
        if (duracion == null || duracion.isBlank() || unidad == null || unidad.isBlank()) {
            return null;
        }

        int valor;
        try {
            valor = Integer.parseInt(duracion.trim());
        } catch (NumberFormatException e) {
            return null;
        }
        if (valor <= 0) {
            return null;
        }

        // Las unidades llegan como "Mes(es)", "día(s)", "Hora(s)"; se comparan por prefijo en
        // minúsculas para no depender de la tilde ni de las mayúsculas del proveedor.
        String u = unidad.trim().toLowerCase();
        if (u.startsWith("mes")) {
            return valor;
        }
        if (u.startsWith("día") || u.startsWith("dia")) {
            return (int) Math.ceil(valor / DIAS_POR_MES);
        }
        if (u.startsWith("semana")) {
            return (int) Math.ceil(valor * 7 / DIAS_POR_MES);
        }
        if (u.startsWith("año") || u.startsWith("ano")) {
            return valor * 12;
        }
        return null;
    }

    // SECOP entrega urlproceso a veces como objeto {url, description} y otras como string plano.
    // Público porque el adaptador también lo necesita al resolver la URL de un proceso suelto.
    public String extractUrl(JsonNode urlProceso) {
        if (urlProceso == null || urlProceso.isNull()) {
            return null;
        }
        if (urlProceso.isTextual()) {
            return urlProceso.asText();
        }
        JsonNode urlField = urlProceso.get("url");
        return urlField != null && !urlField.isNull() ? urlField.asText() : null;
    }
}
