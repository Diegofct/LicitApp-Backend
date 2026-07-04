package com.elemental.licitapp.InteligenciaArtificial.infrastructure.out.anthropic;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.ContentBlockParam;
import com.anthropic.models.messages.DocumentBlockParam;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.TextBlockParam;
import com.elemental.licitapp.Exception.ProcesamientoPliegoException;
import com.elemental.licitapp.InteligenciaArtificial.application.ports.out.ExtractorDocumentosPort;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.DatosRupExtraidos;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.RequisitosPliegoExtraidos;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador del proveedor LLM. Es el unico punto del codigo que conoce el SDK de Anthropic.
 * Envia el PDF como documento nativo (multimodal, preserva tablas) y pide la extraccion en
 * JSON; deserializa la respuesta a {@link DatosRupExtraidos} con el ObjectMapper de Spring
 * (que ya soporta java.time). El modelo NO ve indicadores derivados que deba calcular:
 * solo los lee del documento.
 */
@Component
public class AnthropicExtractorAdapter implements ExtractorDocumentosPort {

    private static final String RUTA_PROMPT = "prompts/extraccion-rup.txt";
    private static final String RUTA_PROMPT_PLIEGO = "prompts/extraccion-pliego.txt";

    private final AnthropicClient client;
    private final AnthropicConfig config;
    private final ObjectMapper objectMapper;
    private final String instrucciones;
    private final String instruccionesPliego;

    public AnthropicExtractorAdapter(AnthropicClient client, AnthropicConfig config, ObjectMapper objectMapper) {
        this.client = client;
        this.config = config;
        this.objectMapper = objectMapper;
        this.instrucciones = cargarPrompt(RUTA_PROMPT);
        this.instruccionesPliego = cargarPrompt(RUTA_PROMPT_PLIEGO);
    }

    @Override
    public DatosRupExtraidos extraerDatosRup(byte[] pdf) {
        String json = invocarModelo(pdf, instrucciones,
                "Extrae los datos del RUP adjunto y responde unicamente con el objeto JSON, sin texto adicional ni formato markdown.");
        return deserializar(json, DatosRupExtraidos.class);
    }

    @Override
    public RequisitosPliegoExtraidos extraerRequisitosPliego(byte[] pdf) {
        String json = invocarModelo(pdf, instruccionesPliego,
                "Extrae los requisitos habilitantes del pliego adjunto y responde unicamente con el objeto JSON, sin texto adicional ni formato markdown.");
        return deserializar(json, RequisitosPliegoExtraidos.class);
    }

    /**
     * Envia el PDF como documento nativo + una instruccion de texto y devuelve el texto crudo
     * de la respuesta. Centraliza el manejo de errores del proveedor.
     */
    private String invocarModelo(byte[] pdf, String systemPrompt, String instruccionUsuario) {
        String base64 = Base64.getEncoder().encodeToString(pdf);

        DocumentBlockParam documento = DocumentBlockParam.builder()
                .base64Source(base64)
                .build();

        MessageCreateParams params = MessageCreateParams.builder()
                .model(config.getModelo())
                .maxTokens(config.getMaxTokens())
                .system(systemPrompt)
                .addUserMessageOfBlockParams(List.of(
                        ContentBlockParam.ofDocument(documento),
                        ContentBlockParam.ofText(TextBlockParam.builder()
                                .text(instruccionUsuario)
                                .build())))
                .build();

        try {
            Message respuesta = client.messages().create(params);
            return respuesta.content().stream()
                    .flatMap(bloque -> bloque.text().stream())
                    .map(bloque -> bloque.text())
                    .collect(Collectors.joining());
        } catch (RuntimeException ex) {
            throw new ProcesamientoPliegoException("Fallo al procesar el documento con el proveedor de IA. Intente de nuevo.", ex);
        }
    }

    private <T> T deserializar(String respuesta, Class<T> tipo) {
        String json = limpiar(respuesta);
        try {
            return objectMapper.readValue(json, tipo);
        } catch (IOException ex) {
            throw new ProcesamientoPliegoException("La respuesta del proveedor de IA no tiene el formato esperado.", ex);
        }
    }

    /**
     * Quita posibles cercas de codigo markdown (```json ... ```) por si el modelo las agrega,
     * y recorta hasta el objeto JSON exterior.
     */
    private String limpiar(String respuesta) {
        if (respuesta == null) {
            return "";
        }
        String limpio = respuesta.trim();
        int inicio = limpio.indexOf('{');
        int fin = limpio.lastIndexOf('}');
        if (inicio >= 0 && fin > inicio) {
            return limpio.substring(inicio, fin + 1);
        }
        return limpio;
    }

    private String cargarPrompt(String ruta) {
        try {
            return StreamUtils.copyToString(
                    new ClassPathResource(ruta).getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar el prompt de extraccion: " + ruta, ex);
        }
    }
}
