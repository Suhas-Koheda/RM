package dev.haas.rm.model

import dev.haas.rm.model.services.FileProcessService
import dev.haas.rm.model.services.ResumeProcessService
import dev.langchain4j.model.chat.ChatModel
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockMultipartFile
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest
class ResumeProcessServiceTest {

    @Autowired
    private lateinit var fileProcessService: FileProcessService
    
    @Autowired
    private lateinit var chatModel: ChatModel
    
    @Autowired
    private lateinit var resumeProcessService: ResumeProcessService

    private val jobDescription = """
        Create contemporary, safe, and feature-rich Android applications from the ground up with Kotlin
        Comprehend requirements and convert drawings and wireframes into code
        Find and repair issues to increase performance
        Work with the design and testing teams to produce high-performance apps
        Work with APIs and data sources per the requirements
        Assist in the upkeep of code quality, reusability, and continual code evaluation
        Investigate and apply new technologies to enhance the developer experience
        Code unit testing to guarantee bug-free, dependable, and optimal user experience
        Work with team members to design, develop, and launch new features
        Put in place security procedures that are consistent with overall security goals
        Using Kotlin, plan, create, construct, and deliver an end-to-end Android mobile app
        Kotlin Developer Requirements & Skills
        Relevant experience developing Android apps
        Knowledge of the Java and Kotlin programming languages is required to develop clear, legible, and maintainable code
        Knowledge of the Android SDK and Android Studio
        Practical knowledge of mobile app architecture, flow, design, implementation, and release
        Familiarity with agile development techniques and CI/CD pipelines
        Extensive experience developing end-to-end Android mobile apps, including design, development, and deployment to app marketplaces
        Thorough awareness of the app lifecycle, architectural concepts, and design elements
        A problem-solving attitude, analytic talents, and excellent technical and communication skills
        Strong debugging abilities and understanding of quality assurance techniques
        Familiarity with RESTful APIs and XML is required to link the app to backend services
        Understanding the most recent mobile app trends, designs, and upcoming technologies
        Understanding of state management to facilitate user engagement
        Understanding of Agile and DevOps techniques
        Knowledge of version control systems and tools, such as GitHub
        Excellent interpersonal and verbal and written communication abilities
        Excellent time management abilities and the ability to work autonomously
        A critical thinker, analyst, and interested professional who wants to create outstanding Android products
    """.trimIndent()

    @Test
    fun `buildAnalysedResults parses result string correctly`() {
        // Given
        val resultString = "85.0|Add more leadership experience|gpt-4"
        
        // When
        val analysed = resumeProcessService.buildAnalysedResults(resultString)
        println(analysed)
        // Then
        assertTrue(analysed.match == 85.0)
        assertTrue(analysed.suggestions == "Add more leadership experience")
        assertTrue(analysed.modelUsed == "gpt-4")
    }

    @Test
    fun `processUploadRequest with actual PDF resume file`() {
        // Given
        val resumeInputStream = ClassPathResource("/static/Suhas_Koheda_Kotlin_Resume.pdf").inputStream
        val resumeFile = MockMultipartFile(
            "resume", 
            "Suhas_Koheda_Kotlin_Resume.pdf",
            "application/pdf",
            resumeInputStream
        )
        
        val uploadRequest = UploadRequest(resumeFile, jobDescription)
        
        // When
        val result = resumeProcessService.processUploadRequest(uploadRequest)
        
        // Then
        assertNotNull(result)
        assertTrue(result.match >= 0.0)
        assertNotNull(result.suggestions)
        assertNotNull(result.modelUsed)

        println("resulkts :$result")

        // Print results for verification
        println("Match percentage: ${result.match}%")
        println("Suggestions: ${result.suggestions}")
        println("Model used: ${result.modelUsed}")
    }

    @Test
    fun `analyseResume with extracted resume text`() {
        // Given
        val resumeInputStream = ClassPathResource("/static/Suhas_Koheda_Kotlin_Resume.pdf").inputStream
        val resumeFile = MockMultipartFile(
            "resume", 
            "Suhas_Koheda_Kotlin_Resume.pdf",
            "application/pdf",
            resumeInputStream
        )
        
        val resumeText = fileProcessService.processFile(resumeFile)
        
        // When
        val result = resumeProcessService.analyseResume(resumeText, jobDescription)
        
        // Then
        assertNotNull(result)
        assertTrue(result.match >= 0.0)
        assertNotNull(result.suggestions)
        assertNotNull(result.modelUsed)
    }
}
