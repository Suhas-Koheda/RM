package dev.haas.rm.model.services

import dev.haas.rm.model.AnalysedResults
import dev.haas.rm.model.UploadRequest
import org.springframework.stereotype.Service
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

data class OllamaRequest(val model: String, val prompt: String, val stream: Boolean = false)
data class OllamaResponse(@JsonProperty("response") val response: String)

@Service
class ResumeProcessService(
    private val fileProcessService: FileProcessService
) {
    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .build()
    
    private val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
    }

    fun processUploadRequest(uploadRequest: UploadRequest): AnalysedResults {
        println("processUploadRequest called with model: ${uploadRequest.model}")
        val fileData = fileProcessService.processFile(uploadRequest.resumeFile)
        return analyseResume(fileData, uploadRequest.JD, uploadRequest.model)
    }

    fun analyseResume(resume: String, JD: String, model: String? = null): AnalysedResults {
        println("analyseResume called with model: $model")
        
        val analyseTemplate = """
            System: You are a professional recruiter with 20+ years of experience in HR...
            Resume:
            $resume
            
            Job Description:
            $JD
            
            Your task:
            1. Analyze how well the resume matches the job description
            2. Calculate a match percentage between 0 and 100
            3. Provide exactly 10 specific suggestions to improve the resume

            EXTREMELY IMPORTANT - FORMAT INSTRUCTIONS:
            - First: Just the match percentage number (example: 75.0)
            - Second: The pipe character |
            - Third: 10 suggestions formatted with HTML tags
            - Fourth: Another pipe character |
            - Fifth: The text "deepseek-llm"
        """.trimIndent()

        var attempts = 0
        var response: String = ""
        var formattedResponse: String? = null

        while (attempts < 3) {
            attempts++
            println("Attempt $attempts to get formatted response")

            try {
                val modelToUse = model ?: "qwen3:0.6b"
                println("Using model: $modelToUse")
                
                val requestPayload = OllamaRequest(
                    model = modelToUse,
                    prompt = analyseTemplate,
                    stream = false
                )
                
                println("Request payload model: ${requestPayload.model}")
                
                println("Request payload model: ${requestPayload.model}")

                val request = HttpRequest.newBuilder()
                    .uri(URI("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestPayload)))
                    .build()

                val httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                response = objectMapper.readValue(httpResponse.body(), OllamaResponse::class.java).response

                if (response.count { it == '|' } >= 2 && response.matches(Regex(".*\\d+.*\\|.*\\|.*"))) {
                    formattedResponse = response
                    break
                }

                if (!response.contains("|")) {
                    val matchResult = Regex("(\\d+(\\.\\d+)?)").find(response)
                    if (matchResult != null) {
                        val matchPercentage = matchResult.groupValues[1]
                        formattedResponse = "$matchPercentage|$response|${model ?: "qwen3:0.6b"}"
                        break
                    }
                }

                Thread.sleep(1000)
            } catch (e: Exception) {
                println("Error during attempt $attempts: ${e.message}")
                Thread.sleep(1000)
            }
        }

        if (formattedResponse == null) {
            return AnalysedResults(
                match = 0.0,
                suggestions = "Failed to get properly formatted response after $attempts attempts. Raw response: $response",
                modelUsed = "unknown"
            )
        }

        return buildAnalysedResults(formattedResponse)
    }

    fun buildAnalysedResults(results: String): AnalysedResults {
        println("Raw response from model: $results \n")
        val splitResults = results.split("|")
        return try {
            val result = AnalysedResults(
                match = splitResults[0].trim().toDouble(),
                suggestions = splitResults[1].trim(),
                modelUsed = splitResults[2].trim()
            )
            println("Created AnalysedResults with modelUsed: ${result.modelUsed}")
            result
        } catch (e: Exception) {
            AnalysedResults(
                match = 0.0,
                suggestions = "Error parsing response: ${e.message}. Original response: $results",
                modelUsed = "unknown"
            )
        }
    }
}
