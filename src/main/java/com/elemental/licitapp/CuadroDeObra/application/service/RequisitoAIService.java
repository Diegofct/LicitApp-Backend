package com.elemental.licitapp.CuadroDeObra.application.service;

import com.elemental.licitapp.CuadroDeObra.application.ports.out.CuadroDeObraRepositoryPort;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.RequisitoLicitacionRepositoryPort;
import com.elemental.licitapp.CuadroDeObra.domain.dto.RequisitoExtraidoDTO;
import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequisitoAIService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final CuadroDeObraRepositoryPort cuadroDeObraRepositoryPort;
    private final RequisitoLicitacionRepositoryPort requisitoRepositoryPort;

    public RequisitoAIService(ChatClient.Builder chatClientBuilder, 
                              EmbeddingModel embeddingModel,
                              CuadroDeObraRepositoryPort cuadroDeObraRepositoryPort,
                              RequisitoLicitacionRepositoryPort requisitoRepositoryPort) {
        this.embeddingModel = embeddingModel;
        this.cuadroDeObraRepositoryPort = cuadroDeObraRepositoryPort;
        this.requisitoRepositoryPort = requisitoRepositoryPort;
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                    Eres un Consultor Experto en Licitaciones Públicas de Colombia. 
                    Tu especialidad es analizar Pliegos de Condiciones del SECOP II de alta complejidad.
                    Extrae con precisión quirúrgica indicadores financieros (Capital de Trabajo, Patrimonio, n meses) 
                    y requisitos de experiencia técnica (General y Específica).
                    Si un dato no aparece en el contexto, usa 0 o null. NUNCA inventes información.
                    """)
                .build();
    }

    @Transactional
    public RequisitoExtraidoDTO extraerRequisitosDePliego(Long cuadroId, MultipartFile file) {
        log.info("Iniciando Smart ETL para Pliego Extenso (ID {}).", cuadroId);

        CuadroDeObra cuadro = cuadroDeObraRepositoryPort.findById(cuadroId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuadro de obra no encontrado"));

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("pliego_analisis_", ".pdf");
            file.transferTo(tempFile);
            Resource resource = new FileSystemResource(tempFile.toFile());

            // 1. ETL: Extracción Híbrida (Tika -> Fallback PagePdf)
            List<Document> documents = new ArrayList<>();
            try {
                log.info("Intento 1: Extracción con TikaDocumentReader...");
                TikaDocumentReader tikaReader = new TikaDocumentReader(resource);
                documents = tikaReader.get();
                log.info("Tika devolvió {} documentos base.", documents.size());
            } catch (Exception e) {
                log.warn("Tika falló o no detectó texto ({}). Intentando fallback con PagePdf...", e.getMessage());
            }

            // Si Tika no trajo nada o falló, intentamos con PagePdf
            if (documents.isEmpty()) {
                PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageBottomMargin(0)
                        .build();
                PagePdfDocumentReader pageReader = new PagePdfDocumentReader(resource, config);
                documents = pageReader.get();
                log.info("Fallback PagePdf devolvió {} documentos.", documents.size());
            }

            // VALIDACIÓN CRÍTICA: Si después de ambos intentos no hay texto, el PDF es ilegible (imagen/escaneado)
            if (documents.isEmpty() || documents.stream().allMatch(d -> d.getContent() == null || d.getContent().trim().isEmpty())) {
                log.error("ERROR: El documento no contiene texto extraíble. Podría ser una imagen sin OCR.");
                throw new RuntimeException("El PDF no tiene texto legible para la IA. Asegúrate de que no sea una imagen escaneada.");
            }

            // 2. ETL: Transformación (Chunking de alta densidad)
            TokenTextSplitter splitter = new TokenTextSplitter(1200, 350, 5, 20000, true);
            List<Document> chunks = splitter.apply(documents);
            log.info("Generados {} fragmentos para análisis vectorial.", chunks.size());

            // 3. Vector Store Temporal (RAG Local con Ollama)
            VectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
            vectorStore.add(chunks);

            // 4. Estrategia de Búsqueda Multidimensional (Multi-Query Retrieval)
            Set<Document> contextoRelevante = new HashSet<>();
            
            // Búsqueda Financiera
            contextoRelevante.addAll(vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query("Indicadores financieros habilitantes: Capital de Trabajo, Patrimonio neto, Liquidez, n meses/años de vigencia")
                    .topK(15).build()));
            
            // Búsqueda de Experiencia
            contextoRelevante.addAll(vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query("Requisitos de experiencia técnica: general, especifica, contratos requeridos, códigos UNSPSC")
                    .topK(15).build()));
            
            // Búsqueda de Otros Requisitos
            contextoRelevante.addAll(vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query("Capacidad Residual K de contratacion, POE, porcentaje de anticipo, forma de pago")
                    .topK(10).build()));

            String contextoFinal = contextoRelevante.stream()
                    .map(doc -> {
                        String p = doc.getMetadata().getOrDefault("page_number", "N/A").toString();
                        return "[PAG " + p + "]: " + doc.getContent();
                    })
                    .distinct()
                    .collect(Collectors.joining("\n---\n"));

            log.info("Contexto consolidado enviado a GPT-4o: {} caracteres.", contextoFinal.length());

            // 5. Inferencia con Structured Output
            RequisitoExtraidoDTO dto = chatClient.prompt()
                    .user(u -> u.text("""
                                Analiza los fragmentos del pliego de condiciones y extrae los requisitos en el JSON solicitado.
                                
                                CONTEXTO DEL PLIEGO:
                                -------------------
                                {contexto}
                                -------------------
                                
                                INSTRUCCIONES:
                                - ctProceso: Valor del Capital de Trabajo.
                                - patrimonio: Valor del Patrimonio mínimo.
                                - n: Número de meses/años de vigencia solicitados.
                                - contrato: Máximo de contratos para experiencia.
                                - poeAnticipo: Porcentaje de anticipo (ej: 30% -> 0.3).
                                - liquidez: Índice de Liquidez (ej: 1.0).
                                - endeudamiento: Nivel de Endeudamiento (ej: 0.7).
                                - razonCoberturaInteres: Razón de Cobertura de Intereses (ej: 1.5).
                                - rentabilidadPatrimonio: ROE.
                                - rentabilidadActivo: ROA.
                                - Extrae con detalle las descripciones de experiencia técnica.
                                """)
                                .param("contexto", contextoFinal))
                    .call()
                    .entity(RequisitoExtraidoDTO.class);

            if (dto != null) {
                log.info("Extracción completada con éxito. Guardando en DB.");
                this.saveExtractedRequirements(cuadro, dto);
            }

            return dto;

        } catch (IOException e) {
            log.error("Error físico de archivo: {}", e.getMessage());
            throw new RuntimeException("Error al procesar el archivo PDF: " + e.getMessage());
        } finally {
            if (tempFile != null) {
                try { Files.deleteIfExists(tempFile); } catch (IOException ignored) {}
            }
        }
    }

    private void saveExtractedRequirements(CuadroDeObra cuadro, RequisitoExtraidoDTO dto) {
        RequisitoLicitacion requisito = requisitoRepositoryPort.findByCuadroDeObraId(cuadro.getId())
                .orElse(new RequisitoLicitacion());
        
        requisito.setCuadroDeObra(cuadro);
        requisito.setGeneral(dto.getGeneral());
        requisito.setEspecifica1(dto.getEspecifica1());
        requisito.setEspecifica2(dto.getEspecifica2());
        requisito.setSecundaria(dto.getSecundaria());
        requisito.setContrato(dto.getContrato());
        requisito.setCtProceso(dto.getCtProceso());
        requisito.setN(dto.getN() != null ? dto.getN() : 0);
        requisito.setPatrimonio(dto.getPatrimonio());
        requisito.setLiquidez(dto.getLiquidez());
        requisito.setEndeudamiento(dto.getEndeudamiento());
        requisito.setRazonCoberturaInteres(dto.getRazonCoberturaInteres());
        requisito.setRentabilidadPatrimonio(dto.getRentabilidadPatrimonio());
        requisito.setRentabilidadActivo(dto.getRentabilidadActivo());
        requisito.setKResidualProceso(dto.getKResidualProceso());
        requisito.setPoeAnticipo(dto.getPoeAnticipo());

        requisitoRepositoryPort.save(requisito);
        log.info("Requisitos guardados para el CuadroDeObra {}", cuadro.getId());
    }
}
