package com.elemental.licitapp.InteligenciaArtificial.infrastructure.out.anthropic;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion del cliente de Anthropic. La api-key, el modelo y el max-tokens vienen
 * de properties ({@code licitapp.ia.*}); cambiar el modelo (ej. a claude-haiku-4-5) no
 * toca codigo.
 */
@Configuration
@ConfigurationProperties(prefix = "licitapp.ia")
public class AnthropicConfig {

    /** Clave de API de Anthropic (de ANTHROPIC_API_KEY via .env). */
    private String apiKey;
    /** Modelo a usar para la extraccion. */
    private String modelo = "claude-opus-4-8";
    /** Tope de tokens de salida. */
    private long maxTokens = 16000;

    @Bean
    public AnthropicClient anthropicClient() {
        return AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public long getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(long maxTokens) {
        this.maxTokens = maxTokens;
    }
}
