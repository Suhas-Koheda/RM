package dev.haas.rm.controller

import dev.haas.rm.model.UploadRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpMethod

@SpringBootTest
@AutoConfigureMockMvc
class FileUploadControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val jobDescription = """
        Create contemporary, safe, and feature-rich Android applications from the ground up with Kotlin
        Comprehend requirements and convert drawings and wireframes into code
        Find and repair issues to increase performance
        Work with the design and testing teams to produce high-performance apps
    """.trimIndent()

    @Test
    fun `upload endpoint should process resume and return analysis`() {
        // Given
        val resumeInputStream = ClassPathResource("/static/Suhas_Koheda_Kotlin_Resume.pdf").inputStream
        val resumeFile = MockMultipartFile(
            "resumeFile",
            "Suhas_Koheda_Kotlin_Resume.pdf",
            "application/pdf",
            resumeInputStream
        )
        
        // When/Then
        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/analyse/upload")
                .file(resumeFile)
                .param("JD", jobDescription)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.match").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.suggestions").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.modelUsed").exists())
    }
}
