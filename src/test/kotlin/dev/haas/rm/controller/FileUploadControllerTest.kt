package dev.haas.rm.controller

import dev.haas.rm.model.AnalysedResults
import dev.haas.rm.model.ResumeProcessService
import dev.haas.rm.model.UploadRequest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class FileUploadControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var resumeProcessService: ResumeProcessService

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
        
        val mockResult = AnalysedResults(
            match = 85.0,
            suggestions = "Add more leadership experience",
            modelUsed = "gemini-pro"
        )
        
        whenever(resumeProcessService.processUploadRequest(any())).thenReturn(mockResult)
        
        // When/Then
        mockMvc.perform(
            multipart("/analyse/upload")
                .file(resumeFile)
                .param("JD", "Job description text here")
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.match").value(85.0))
            .andExpect(jsonPath("$.suggestions").value("Add more leadership experience"))
            .andExpect(jsonPath("$.modelUsed").value("gemini-pro"))
    }
}
