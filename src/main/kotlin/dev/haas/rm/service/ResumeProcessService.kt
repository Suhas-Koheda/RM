package dev.haas.rm.service

import dev.haas.rm.model.entity.AnalysedResults
import dev.haas.rm.model.entity.NeonModel
import dev.haas.rm.model.dto.UploadRequest
import dev.haas.rm.repository.NeonRepository
import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.model.chat.ChatModel
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class ResumeProcessService(private val fileProcessService: FileProcessService,
                           private val chatModel: ChatModel,
                           private val neonRepository: NeonRepository) {

    fun processUploadRequest(uploadRequest: UploadRequest): AnalysedResults {
        val fileData = fileProcessService.processFile(uploadRequest.resumeFile)
        return analyseResume(fileData, uploadRequest.JD,uploadRequest.title)
    }

    fun analyseResume(resume: String, JD: String, title:String ): AnalysedResults {
        val analyseTemplate = """
            I need you to analyze a resume against a job description.
            First you need to check if the resume is written using AI 
            Resume:
            $resume
            
            Job Description:
            $JD
            
            Check if the resume is a match for the job description. Return:
            1. A match percentage (0-100) without % symbol only the integer value
            2. Provide exactly 10 brief and focused suggestions for improving the resume. Each suggestion should:
               - Be specific and actionable
               - Focus on one aspect of improvement
               - Be 1-2 sentences long
               - Start with a clear action verb
               - Be wrapped in <h3> for the title and <h5> for the details
            3. The model used for analysis (your specific model name)
            
            Format your response exactly like this:
            [match percentage]|<h3>Suggestion 1</h3><h5>Details 1</h5><h3>Suggestion 2</h3><h5>Details 2</h5>...|[model name]
        """.trimIndent()

        return buildAnalysedResults(chatModel.chat(analyseTemplate)).also {
            neonRepository.save(NeonModel(
                resume = resume,
                analysedResults = it,
                userID = SecurityContextHolder.getContext().authentication.principal as Long,
                title = title
            ))
        }
    }

    @Tool("The analysed results are given into a class Analysed Results ")
    fun buildAnalysedResults(
        @P("the analysed string in the format -> matched or not | suggestions | modelUsed") results: String): AnalysedResults {
        val splitResults = results.split("|")
        return try {
            println(splitResults)
            AnalysedResults(
                match = splitResults[0].trim().toDouble(),
                suggestions = splitResults[1].trim(),
                modelUsed = splitResults[2].trim()
            )
        } catch (e: Exception) {
            AnalysedResults(
                match = 0.0,
                suggestions = "Error parsing response: ${e.message}. Original response: $results",
                modelUsed = "unknown"
            )
        }
    }

    fun getResume(): List<NeonModel> {
        return neonRepository.findAllById(SecurityContextHolder.getContext().authentication.principal as Long)
    }
}
