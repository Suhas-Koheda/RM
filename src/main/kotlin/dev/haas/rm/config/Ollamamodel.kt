package dev.haas.rm.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class Ollamamodel {
    @Value("\${ollama.base.url}")
    private lateinit var baseUrl: String

    // No bean definition needed as we're using direct HTTP calls now
    // Configuration is injected directly into OllamaApiService
}

