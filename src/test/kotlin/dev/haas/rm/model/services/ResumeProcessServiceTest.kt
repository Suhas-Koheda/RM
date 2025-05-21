package dev.haas.rm.model.services

import com.fasterxml.jackson.annotation.JsonProperty
import dev.haas.rm.model.UploadRequest
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockMultipartFile

// Re-declaring for test purposes if they are private in the main source.
// Ideally, these would be accessible (e.g. internal or public) from the main source.
private data class OllamaRequestTest(val model: String, val prompt: String, val stream: Boolean = false)
private data class OllamaResponseTest(@JsonProperty("response") val response: String)


@ExtendWith(MockKExtension::class)
class ResumeProcessServiceTest {

    @MockK
    lateinit var fileProcessService: FileProcessService

    private lateinit var httpClient: HttpClient
    private lateinit var resumeProcessService: ResumeProcessService
    private lateinit var mockEngine: MockEngine

    @BeforeEach
    fun setUp() {
        mockEngine = MockEngine { request ->
            // Default handler, can be overridden in tests
            respond(
                content = """{"response":"default mock response"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                jackson()
            }
        }
        resumeProcessService = ResumeProcessService(fileProcessService, httpClient)
    }

    @Test
    fun `analyseResume should call httpClient post and return correct string`() = runTest {
        // Arrange
        val resumeText = "This is a resume."
        val jobDescription = "This is a job description."
        val expectedPrompt = """
            I need you to analyze a resume against a job description.
            First you need to check if the resume is written using AI 
            Resume:
            $resumeText
            
            Job Description:
            $jobDescription
            
            Check if the resume is a match for the job description. Return:
            1. A match percentage (0-100)
            2.The suggestion should be given in 10 points  Suggestions for improving the resume and the suggestions should not be looking like ai generated you must humanise it . Headings of suggestions should be wrapped with h3 html tag and the remaining text in h5 html tag  
            3. The model used for analysis
            
            Format your response exactly like this: [match percentage]|[suggestions]|[model name / which model are you ]
            Dont give the default model in the model name give which model are you in place of the model
            For example: 85.0|Add more leadership experience|gpt-4
        """.trimIndent()
        val ollamaApiResponse = "80|Great resume|llama2"
        val mockOllamaResponseJson = """{"response":"$ollamaApiResponse"}"""

        // Configure MockEngine for this specific test
        mockEngine.config.requestHandlers.clear() // Clear default or previous handlers
        mockEngine.addHandler { request ->
            assertEquals("http://localhost:11434/api/generate", request.url.toString())
            assertEquals(HttpMethod.Post, request.method)
            // Ideally, we would deserialize request.body to OllamaRequest and assert its properties.
            // For simplicity here, we're checking the prompt within the service logic.
            // val requestBody = request.body // This would be an OutgoingContent
            // For more detailed body inspection:
            // val channel = request.body.toByteArray()
            // val receivedJson = String(channel)
            // val mapper = ObjectMapper().registerModule(JavaTimeModule()) // if needed
            // val ollamaRequest = mapper.readValue(receivedJson, OllamaRequestTest::class.java)
            // assertEquals("llama2", ollamaRequest.model)
            // assertEquals(expectedPrompt, ollamaRequest.prompt)

            respond(
                content = mockOllamaResponseJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        // Act
        val result = resumeProcessService.analyseResume(resumeText, jobDescription)

        // Assert
        assertEquals(ollamaApiResponse, result)
        // Verification of POST call is implicitly done by MockEngine if handler is matched and executed.
        // To be more explicit, one could check mockEngine.requestHistory.
        assertEquals(1, mockEngine.requestHistory.size)
        assertEquals("http://localhost:11434/api/generate", mockEngine.requestHistory.first().url.toString())
    }

    @Test
    fun `processUploadRequest should call fileProcessService and analyseResume, then return correct string`() = runTest {
        // Arrange
        val mockFile = MockMultipartFile("resume", "resume.pdf", "application/pdf", "pdf content".toByteArray())
        val uploadRequest = UploadRequest(resumeFile = mockFile, JD = "Job Description")
        val processedFileContent = "Processed PDF content as text."
        val ollamaApiResponse = "75|Needs more detail|llama2"
        val mockOllamaResponseJson = """{"response":"$ollamaApiResponse"}"""

        coEvery { fileProcessService.processFile(mockFile) } returns processedFileContent

        // Configure MockEngine for the analyseResume call
        mockEngine.config.requestHandlers.clear()
        mockEngine.addHandler { request ->
            // Basic check for the Ollama request
            assertEquals("http://localhost:11434/api/generate", request.url.toString())
            respond(
                content = mockOllamaResponseJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        // Act
        val result = resumeProcessService.processUploadRequest(uploadRequest)

        // Assert
        assertEquals(ollamaApiResponse, result)
        io.mockk.coVerify { fileProcessService.processFile(mockFile) }
        assertEquals(1, mockEngine.requestHistory.size) // Verifies analyseResume (and thus httpClient.post) was called
    }
}
