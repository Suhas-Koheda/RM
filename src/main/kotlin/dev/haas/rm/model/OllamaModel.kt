package dev.haas.rm.model

data class OllamaModelsResponse(val models: List<OllamaModel>)

data class OllamaModel(
    val name: String,
    val model: String,
    val modified_at: String,
    val size: Long,
    val digest: String,
    val details: OllamaModelDetails
)

data class OllamaModelDetails(
    val parent_model: String,
    val format: String,
    val family: String,
    val families: List<String>,
    val parameter_size: String,
    val quantization_level: String
)

data class ModelInfo(
    val name: String,
    val displayName: String,
    val family: String,
    val parameterSize: String
)
