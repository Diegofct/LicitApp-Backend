package com.elemental.licitapp.InteligenciaArtificial.application.service;

import com.elemental.licitapp.Exception.PliegoIlegibleException;
import com.elemental.licitapp.InteligenciaArtificial.application.ports.in.ExtraerRequisitosPliegoUseCase;
import com.elemental.licitapp.InteligenciaArtificial.application.ports.out.ExtractorDocumentosPort;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.RequisitosPliegoExtraidos;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccionPliego;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Orquesta la extraccion de requisitos del pliego: llama al modelo, valida y normaliza el
 * resultado y acumula advertencias. Nunca persiste: produce un borrador editable.
 *
 * <p>Servicio aparte del {@code ExtraccionDocumentosAppService} (RUP) para no tocar ese flujo.
 * El recorte del PDF a las paginas relevantes ya lo hizo el llamador (CuadroDeObra); aqui el
 * PDF llega listo para el modelo.
 */
@Service
public class ExtraccionPliegoAppService implements ExtraerRequisitosPliegoUseCase {

    private final ExtractorDocumentosPort extractor;

    public ExtraccionPliegoAppService(ExtractorDocumentosPort extractor) {
        this.extractor = extractor;
    }

    @Override
    public ResultadoExtraccionPliego extraer(byte[] pdfEnsamblado, String nombreArchivo) {
        RequisitosPliegoExtraidos crudos = extractor.extraerRequisitosPliego(pdfEnsamblado);
        if (crudos == null || todosNulos(crudos)) {
            throw new PliegoIlegibleException(
                    "No se encontraron requisitos en las paginas indicadas; revise la seleccion de paginas.");
        }

        List<String> advertencias = new ArrayList<>();

        // Normalizacion defensiva de los ratios que deben ir en [0,1]: si llegan > 1 se asumen
        // porcentaje (ej. 70 en vez de 0.70) y se dividen entre 100. liquidez y RCI NO se tocan
        // (legitimamente pueden ser > 1).
        Double endeudamiento = normalizarPorcentaje(crudos.endeudamiento(), "endeudamiento", advertencias);
        Double rentabilidadPatrimonio = normalizarPorcentaje(crudos.rentabilidadPatrimonio(), "rentabilidadPatrimonio", advertencias);
        Double rentabilidadActivo = normalizarPorcentaje(crudos.rentabilidadActivo(), "rentabilidadActivo", advertencias);
        Double poeAnticipo = normalizarPorcentaje(crudos.poeAnticipo(), "poeAnticipo", advertencias);

        // Advertencias por valores negativos.
        advertirSiNegativo(crudos.presupuesto(), "presupuesto", advertencias);
        advertirSiNegativo(crudos.patrimonio(), "patrimonio", advertencias);
        advertirSiNegativo(crudos.capitalTrabajo(), "capitalTrabajo", advertencias);
        advertirSiNegativo(crudos.liquidez(), "liquidez", advertencias);
        advertirSiNegativo(endeudamiento, "endeudamiento", advertencias);
        advertirSiNegativo(crudos.razonCoberturaInteres(), "razonCoberturaInteres", advertencias);
        advertirSiNegativo(rentabilidadPatrimonio, "rentabilidadPatrimonio", advertencias);
        advertirSiNegativo(rentabilidadActivo, "rentabilidadActivo", advertencias);
        advertirSiNegativo(crudos.kResidualProceso(), "kResidualProceso", advertencias);
        advertirSiNegativo(poeAnticipo, "poeAnticipo", advertencias);

        // Ratios que deben quedar en [0,1]; si siguen fuera, posible confusion porcentaje/decimal.
        advertirSiFueraDeRatio(endeudamiento, "endeudamiento", advertencias);
        advertirSiFueraDeRatio(rentabilidadPatrimonio, "rentabilidadPatrimonio", advertencias);
        advertirSiFueraDeRatio(rentabilidadActivo, "rentabilidadActivo", advertencias);
        advertirSiFueraDeRatio(poeAnticipo, "poeAnticipo", advertencias);

        // Advertencias por campos no encontrados.
        advertirSiNulo(crudos.general(), "general", advertencias);
        advertirSiNulo(crudos.especifica1(), "especifica1", advertencias);
        advertirSiNulo(crudos.presupuesto(), "presupuesto", advertencias);
        advertirSiNulo(crudos.patrimonio(), "patrimonio", advertencias);
        advertirSiNulo(crudos.capitalTrabajo(), "capitalTrabajo", advertencias);
        advertirSiNulo(crudos.liquidez(), "liquidez", advertencias);
        advertirSiNulo(endeudamiento, "endeudamiento", advertencias);
        advertirSiNulo(crudos.razonCoberturaInteres(), "razonCoberturaInteres", advertencias);
        advertirSiNulo(rentabilidadPatrimonio, "rentabilidadPatrimonio", advertencias);
        advertirSiNulo(rentabilidadActivo, "rentabilidadActivo", advertencias);
        advertirSiNulo(crudos.kResidualProceso(), "kResidualProceso", advertencias);

        RequisitosPliegoExtraidos datos = new RequisitosPliegoExtraidos(
                crudos.general(),
                crudos.especifica1(),
                crudos.especifica2(),
                crudos.secundaria(),
                crudos.contrato(),
                crudos.n(),
                crudos.presupuesto(),
                crudos.patrimonio(),
                crudos.capitalTrabajo(),
                crudos.liquidez(),
                endeudamiento,
                crudos.razonCoberturaInteres(),
                rentabilidadPatrimonio,
                rentabilidadActivo,
                crudos.kResidualProceso(),
                poeAnticipo
        );
        return new ResultadoExtraccionPliego(datos, advertencias);
    }

    private boolean todosNulos(RequisitosPliegoExtraidos r) {
        return r.general() == null && r.especifica1() == null && r.especifica2() == null
                && r.secundaria() == null && r.contrato() == null && r.n() == null
                && r.presupuesto() == null && r.patrimonio() == null && r.capitalTrabajo() == null
                && r.liquidez() == null && r.endeudamiento() == null && r.razonCoberturaInteres() == null
                && r.rentabilidadPatrimonio() == null && r.rentabilidadActivo() == null
                && r.kResidualProceso() == null && r.poeAnticipo() == null;
    }

    private Double normalizarPorcentaje(Double valor, String campo, List<String> advertencias) {
        if (valor == null || valor <= 1.0) {
            return valor;
        }
        Double normalizado = valor / 100.0;
        advertencias.add("El campo '" + campo + "' venia como " + valor
                + "; se asumio porcentaje y se normalizo a " + normalizado + ".");
        return normalizado;
    }

    private void advertirSiFueraDeRatio(Double valor, String campo, List<String> advertencias) {
        if (valor != null && valor > 1.0) {
            advertencias.add("El campo '" + campo + "' (" + valor
                    + ") quedo fuera de [0,1]: revisar si es porcentaje o decimal.");
        }
    }

    private void advertirSiNegativo(Double valor, String campo, List<String> advertencias) {
        if (valor != null && valor < 0) {
            advertencias.add("El campo '" + campo + "' es negativo: revisar.");
        }
    }

    private void advertirSiNulo(Object valor, String campo, List<String> advertencias) {
        if (valor == null || (valor instanceof String s && s.isBlank())) {
            advertencias.add("No se encontro el campo '" + campo + "' en las paginas indicadas.");
        }
    }
}
