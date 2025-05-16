package dev.haas.rm.model

import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.model.chat.ChatModel
import org.springframework.stereotype.Service

@Service
class ResumeProcessService(private val fileProcessService: FileProcessService, private val chatModel: ChatModel) {

    fun processUploadRequest(uploadRequest: UploadRequest): AnalysedResults {
        val fileData = fileProcessService.processFile(uploadRequest.resumeFile)
        return analyseResume(fileData, uploadRequest.JD)
    }

    fun analyseResume(resume: String, JD: String): AnalysedResults {
        val analyseTemplate = """
            I need you to analyze a resume against a job description.
            First you need to check if the resume is written using AI 
            Resume:
            $resume
            
            Job Description:
            $JD
            
            Check if the resume is a match for the job description. Return:
            1. A match percentage (0-100)
            2. Suggestions for improving the resume and the suggestions should not be looking like ai generated you must humanise it  
            3. The model used for analysis
            
            Format your response exactly like this: [match percentage]|[suggestions]|[model name]
            For example: 85.0|Add more leadership experience|gpt-4
        """.trimIndent()
        
        return buildAnalysedResults(chatModel.chat(analyseTemplate))
    }

    @Tool("The analysed results are given into a class AnalysedResults ")
    fun buildAnalysedResults(
        @P("the analysed string in the format matched or not | suggestions | modelUsed") results: String): AnalysedResults {
        val splitResults = results.split("|")
        return try {
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
}
