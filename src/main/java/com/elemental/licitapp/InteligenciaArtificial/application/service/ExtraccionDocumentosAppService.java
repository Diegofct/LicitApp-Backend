package com.elemental.licitapp.InteligenciaArtificial.application.service;

import com.elemental.licitapp.Empresa.domain.entity.IndicadoresFinancieros;
import com.elemental.licitapp.Exception.PliegoIlegibleException;
import com.elemental.licitapp.InteligenciaArtificial.application.ports.in.ExtraerDatosRupUseCase;
import com.elemental.licitapp.InteligenciaArtificial.application.ports.out.ExtractorDocumentosPort;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.CierreFiscalExtraido;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.DatosRupExtraidos;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ExperienciaExtraida;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.InformacionGeneralRup;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccion;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquesta la extraccion del RUP: valida el archivo, llama al modelo, valida y normaliza
 * el resultado y acumula advertencias. Nunca persiste: produce un borrador editable.
 */
@Service
public class ExtraccionDocumentosAppService implements ExtraerDatosRupUseCase {

    private static final int ANIO_MINIMO = 2000;
    /** Tolerancia de la verificacion cruzada financiera: 1%. */
    private static final BigDecimal TOLERANCIA_RELATIVA = new BigDecimal("0.01");

    private final ExtractorDocumentosPort extractor;

    public ExtraccionDocumentosAppService(ExtractorDocumentosPort extractor) {
        this.extractor = extractor;
    }

    @Override
    public ResultadoExtraccion extraer(byte[] pdf, String nombreArchivo) {
        validarArchivo(pdf);

        DatosRupExtraidos crudos = extractor.extraerDatosRup(pdf);

        List<String> advertencias = new ArrayList<>();
        InformacionGeneralRup general = normalizarYValidarGeneral(crudos.informacionGeneral(), advertencias);
        List<CierreFiscalExtraido> cierres = validarCierres(crudos.cierresFiscales(), advertencias);
        List<ExperienciaExtraida> experiencias = validarExperiencias(crudos.experiencias(), advertencias);

        DatosRupExtraidos datos = new DatosRupExtraidos(general, cierres, experiencias);
        return new ResultadoExtraccion(datos, advertencias);
    }

    // ----- Validacion del archivo -----

    private void validarArchivo(byte[] pdf) {
        if (pdf == null || pdf.length == 0) {
            throw new PliegoIlegibleException("El archivo esta vacio.");
        }
        // Firma PDF: %PDF
        boolean firmaValida = pdf.length >= 4
                && pdf[0] == 0x25 && pdf[1] == 0x50 && pdf[2] == 0x44 && pdf[3] == 0x46;
        if (!firmaValida) {
            throw new PliegoIlegibleException("El archivo no es un PDF valido.");
        }
    }

    // ----- Informacion general -----

    private InformacionGeneralRup normalizarYValidarGeneral(InformacionGeneralRup general, List<String> advertencias) {
        if (general == null) {
            throw new PliegoIlegibleException("No se pudo extraer informacion general; el documento probablemente no es un RUP.");
        }

        String nitNormalizado = normalizarNit(general.nit());

        // 5.4.1: si faltan NIT y razon social, probablemente no es un RUP.
        if (esVacio(nitNormalizado) && esVacio(general.razonSocial())) {
            throw new PliegoIlegibleException("El documento no contiene NIT ni razon social; probablemente no es un RUP.");
        }

        advertirSiNulo(general.nit(), "nit", advertencias);
        advertirSiNulo(general.razonSocial(), "razonSocial", advertencias);
        advertirSiNulo(general.numeroProponenteCcb(), "numeroProponenteCcb", advertencias);
        advertirSiNulo(general.tamanoEmpresa(), "tamanoEmpresa", advertencias);
        advertirSiNulo(general.representanteLegal(), "representanteLegal", advertencias);
        advertirSiNulo(general.correo(), "correo", advertencias);

        // Fechas coherentes: inscripcion <= ultimaRenovacion <= hoy.
        LocalDate inscripcion = general.fechaInscripcion();
        LocalDate renovacion = general.fechaUltimaRenovacion();
        LocalDate hoy = LocalDate.now();
        if (inscripcion != null && renovacion != null && inscripcion.isAfter(renovacion)) {
            advertencias.add("La fecha de inscripcion es posterior a la de ultima renovacion: revisar.");
        }
        if (renovacion != null && renovacion.isAfter(hoy)) {
            advertencias.add("La fecha de ultima renovacion es futura: revisar.");
        }
        if (inscripcion != null && inscripcion.isAfter(hoy)) {
            advertencias.add("La fecha de inscripcion es futura: revisar.");
        }

        return new InformacionGeneralRup(
                nitNormalizado,
                general.razonSocial(),
                general.numeroProponenteCcb(),
                general.tamanoEmpresa(),
                general.representanteLegal(),
                general.identificacionRepresentanteLegal(),
                general.fechaInscripcion(),
                general.fechaUltimaRenovacion(),
                general.direccion(),
                general.telefono(),
                general.correo()
        );
    }

    /**
     * Deja solo digitos (incluido el de verificacion), sin guion ni espacios.
     * Ej. "245615365-1" -> "2456153651".
     */
    private String normalizarNit(String nit) {
        if (nit == null) {
            return null;
        }
        String soloDigitos = nit.replaceAll("\\D", "");
        return soloDigitos.isEmpty() ? null : soloDigitos;
    }

    // ----- Cierres fiscales -----

    private List<CierreFiscalExtraido> validarCierres(List<CierreFiscalExtraido> cierres, List<String> advertencias) {
        if (cierres == null || cierres.isEmpty()) {
            advertencias.add("No se encontraron cierres fiscales en el documento.");
            return List.of();
        }
        int anioActual = Year.now().getValue();
        for (CierreFiscalExtraido c : cierres) {
            String etiqueta = c.anioCierre() != null ? "cierre " + c.anioCierre() : "un cierre fiscal";

            if (c.anioCierre() == null) {
                advertencias.add("Hay un cierre fiscal sin ano reportado.");
            } else if (c.anioCierre() < ANIO_MINIMO || c.anioCierre() > anioActual) {
                advertencias.add("El ano del " + etiqueta + " esta fuera de rango (" + ANIO_MINIMO + "-" + anioActual + ").");
            }

            advertirSiNegativo(c.activoCorriente(), "activoCorriente", etiqueta, advertencias);
            advertirSiNegativo(c.pasivoCorriente(), "pasivoCorriente", etiqueta, advertencias);
            advertirSiNegativo(c.activoTotal(), "activoTotal", etiqueta, advertencias);
            advertirSiNegativo(c.pasivoTotal(), "pasivoTotal", etiqueta, advertencias);
            advertirSiNegativo(c.gastosInteres(), "gastosInteres", etiqueta, advertencias);

            verificacionCruzada(c, etiqueta, advertencias);
        }
        return cierres;
    }

    /**
     * Recalcula los indicadores con los absolutos extraidos y los compara contra los
     * impresos en el RUP. Una diferencia relativa > 1% sugiere un error de lectura.
     */
    private void verificacionCruzada(CierreFiscalExtraido c, String etiqueta, List<String> advertencias) {
        IndicadoresFinancieros ind = new IndicadoresFinancieros();
        ind.setAnioCierre(c.anioCierre());
        ind.setActivoCorriente(c.activoCorriente());
        ind.setPasivoCorriente(c.pasivoCorriente());
        ind.setActivoTotal(c.activoTotal());
        ind.setPasivoTotal(c.pasivoTotal());
        ind.setUtilidadOperacional(c.utilidadOperacional());
        ind.setGastosInteres(c.gastosInteres());
        ind.recalcular();

        compararIndicador("liquidez", ind.getLiquidez(), c.liquidezImpresa(), etiqueta, advertencias);
        compararIndicador("endeudamiento", ind.getEndeudamiento(), c.endeudamientoImpreso(), etiqueta, advertencias);
        compararIndicador("razon de cobertura de intereses", ind.getRazonCoberturaInteres(), c.razonCoberturaInteresImpresa(), etiqueta, advertencias);
        compararIndicador("rentabilidad del patrimonio", ind.getRentabilidadPatrimonio(), c.rentabilidadPatrimonioImpresa(), etiqueta, advertencias);
        compararIndicador("rentabilidad del activo", ind.getRentabilidadActivo(), c.rentabilidadActivoImpresa(), etiqueta, advertencias);
    }

    private void compararIndicador(String nombre, BigDecimal calculado, BigDecimal impreso,
                                   String etiqueta, List<String> advertencias) {
        if (calculado == null || impreso == null || impreso.signum() == 0) {
            return;
        }
        BigDecimal diferenciaRelativa = calculado.subtract(impreso).abs()
                .divide(impreso.abs(), 6, RoundingMode.HALF_UP);
        if (diferenciaRelativa.compareTo(TOLERANCIA_RELATIVA) > 0) {
            advertencias.add(String.format(
                    "En el %s, la %s impresa en el RUP (%s) no coincide con la calculada (%s): revisar los valores absolutos.",
                    etiqueta, nombre, impreso.toPlainString(), calculado.toPlainString()));
        }
    }

    // ----- Experiencia -----

    private List<ExperienciaExtraida> validarExperiencias(List<ExperienciaExtraida> experiencias, List<String> advertencias) {
        if (experiencias == null) {
            return List.of();
        }
        for (ExperienciaExtraida e : experiencias) {
            Double porcentaje = e.porcentajeParticipacion();
            if (porcentaje != null && (porcentaje <= 0 || porcentaje > 100)) {
                advertencias.add("Un contrato de experiencia tiene un porcentaje de participacion fuera de (0, 100]: " + porcentaje + ".");
            }
            if (e.valorSMMLV() != null && e.valorSMMLV() < 0) {
                advertencias.add("Un contrato de experiencia tiene un valor en SMMLV negativo.");
            }
        }
        return experiencias;
    }

    // ----- Utilidades -----

    private boolean esVacio(String s) {
        return s == null || s.isBlank();
    }

    private void advertirSiNulo(String valor, String campo, List<String> advertencias) {
        if (esVacio(valor)) {
            advertencias.add("No se encontro el campo '" + campo + "' en el documento.");
        }
    }

    private void advertirSiNegativo(BigDecimal valor, String campo, String etiqueta, List<String> advertencias) {
        if (valor != null && valor.signum() < 0) {
            advertencias.add("En el " + etiqueta + ", el campo '" + campo + "' es negativo: revisar.");
        }
    }
}
