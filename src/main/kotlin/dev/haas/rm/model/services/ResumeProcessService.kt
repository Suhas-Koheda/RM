package dev.haas.rm.model.services

import com.fasterxml.jackson.annotation.JsonProperty
import dev.haas.rm.model.UploadRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.springframework.stereotype.Service

private data class OllamaRequest(val model: String, val prompt: String, val stream: Boolean = false)
private data class OllamaResponse(@JsonProperty("response") val response: String)

@Service
class ResumeProcessService(
    private val fileProcessService: FileProcessService,
    private val httpClient: HttpClient
) {

    suspend fun processUploadRequest(uploadRequest: UploadRequest): String {
        val fileData = fileProcessService.processFile(uploadRequest.resumeFile)
        return analyseResume(fileData, uploadRequest.JD)
    }

    suspend fun analyseResume(resume: String, JD: String): String {
        val analyseTemplate = """
            I need you to analyze a resume against a job description.
            First you need to check if the resume is written using AI 
            Resume:
            $resume
            
            Job Description:
            $JD
            
            Check if the resume is a match for the job description. Return:
            1. A match percentage (0-100)
            2.The suggestion should be given in 10 points  Suggestions for improving the resume and the suggestions should not be looking like ai generated you must humanise it . Headings of suggestions should be wrapped with h3 html tag and the remaining text in h5 html tag  
            3. The model used for analysis
            
            Format your response exactly like this: [match percentage]|[suggestions]|[model name / which model are you ]
            Dont give the default model in the model name give which model are you in place of the model
            For example: 85.0|Add more leadership experience|gpt-4
        """.trimIndent()

        val requestPayload = OllamaRequest(model = "llama2", prompt = analyseTemplate)

        val response: OllamaResponse = httpClient.post("http://localhost:11434/api/generate") {
            contentType(ContentType.Application.Json)
            setBody(requestPayload)
        }.body()

        return response.response
    }
}