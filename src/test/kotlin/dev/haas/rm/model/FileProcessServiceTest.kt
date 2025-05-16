package dev.haas.rm.model

import dev.haas.rm.model.services.FileProcessService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockMultipartFile

class FileProcessServiceTest {

    @Test
    fun `processFile should extract text from PDF file`() {
        // Given
        val service = FileProcessService()
        val resumeInputStream = ClassPathResource("/static/Suhas_Koheda_Kotlin_Resume.pdf").inputStream
        val file = MockMultipartFile(
            "resume", 
            "Suhas_Koheda_Kotlin_Resume.pdf", 
            "application/pdf", 
            resumeInputStream
        )
        
        // When
        val result = service.processFile(file)
        
        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        println("Extracted text length: ${result.length}")
        
        // Verify content contains expected keywords from a resume
        val keywords = listOf("Kotlin", "Android", "development", "Java", "experience")
        keywords.forEach { keyword ->
            assertTrue(result.contains(keyword, ignoreCase = true), "Result should contain keyword: $keyword")
        }
    }
    
    @Test
    fun `processFile should handle large PDF files`() {
        // Given
        val service = FileProcessService()
        val resumeInputStream = ClassPathResource("/static/Suhas_Koheda_Kotlin_Resume.pdf").inputStream
        val file = MockMultipartFile(
            "resume", 
            "Suhas_Koheda_Kotlin_Resume.pdf", 
            "application/pdf", 
            resumeInputStream
        )
        
        // When
        val startTime = System.currentTimeMillis()
        val result = service.processFile(file)
        val endTime = System.currentTimeMillis()
        val processingTime = endTime - startTime
        
        // Then
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        println("Processing time: $processingTime ms")
        
        // Ensure processing is reasonably fast
        assertTrue(processingTime < 5000, "PDF processing should complete within 5 seconds")
    }
}
