package dev.haas.rm.model.services

import dev.haas.rm.model.ModelInfo
import dev.haas.rm.model.OllamaModel
import dev.haas.rm.model.OllamaModelsResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.slf4j.LoggerFactory

@Service
class ModelService(private val restTemplate: RestTemplate) {

    private val logger = LoggerFactory.getLogger(ModelService::class.java)
    
    // Use localhost:11434 for direct access to Ollama
    private val baseUrl = "http://localhost:11434"

    fun getAvailableModels(): List<ModelInfo> {
        try {
            logger.info("Fetching models from Ollama at: $baseUrl/api/tags")
            val response = restTemplate.getForObject("$baseUrl/api/tags", OllamaModelsResponse::class.java)
            val models = response?.models?.map { mapToModelInfo(it) } ?: emptyList()
            logger.info("Retrieved ${models.size} models from Ollama")
            return models
        } catch (e: Exception) {
            logger.error("Error fetching models from Ollama at $baseUrl/api/tags: ${e.message}", e)
            return emptyList()
        }
    }

    private fun mapToModelInfo(ollamaModel: OllamaModel): ModelInfo {
        return ModelInfo(
            name = ollamaModel.name,
            displayName = ollamaModel.name,
            family = ollamaModel.details.family,
            parameterSize = ollamaModel.details.parameter_size
        )
    }
}