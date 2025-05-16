package dev.haas.rm.model.services

import dev.haas.rm.model.AnalysedResults
import dev.haas.rm.model.UploadRequest
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
            2.The suggestion should be given in 10 points  Suggestions for improving the resume and the suggestions should not be looking like ai generated you must humanise it . Headings of suggestions should be wrapped with h3 html tag and the remaining text in h5 html tag  
            3. The model used for analysis
            
            Format your response exactly like this: [match percentage]|[suggestions]|[model name / which model are you ]
            Dont give the default model in the model name give which model are you in place of the model
            For example: 85.0|Add more leadership experience|gpt-4
        """.trimIndent()

        return buildAnalysedResults(chatModel.chat(analyseTemplate))
    }

    @Tool("The analysed results are given into a class AnalysedResults ")
    fun buildAnalysedResults(
        @P("the analysed string in the format matched or not | suggestions | modelUsed") results: String): AnalysedResults {
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
}