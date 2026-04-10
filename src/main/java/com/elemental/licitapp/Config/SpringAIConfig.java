package com.elemental.licitapp.Config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SpringAIConfig {

    /**
     * Marca OpenAI como el ChatModel primario para que ChatClient.Builder lo use por defecto.
     */
    @Bean
    @Primary
    public ChatModel primaryChatModel(@Qualifier("openAiChatModel") ChatModel openAiChatModel) {
        return openAiChatModel;
    }

    /**
     * Marca Ollama como el EmbeddingModel primario para procesos de vectores (RAG).
     */
    @Bean
    @Primary
    public EmbeddingModel primaryEmbeddingModel(@Qualifier("ollamaEmbeddingModel") EmbeddingModel ollamaEmbeddingModel) {
        return ollamaEmbeddingModel;
    }
}
