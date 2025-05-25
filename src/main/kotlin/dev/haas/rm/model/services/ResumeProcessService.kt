package dev.haas.rm.model.services

import dev.haas.rm.model.AnalysedResults
import dev.haas.rm.model.NeonModel
import dev.haas.rm.model.UploadRequest
import dev.haas.rm.model.repository.NeonRepository
import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.model.chat.ChatModel
import org.springframework.stereotype.Service

@Service
class ResumeProcessService(private val fileProcessService: FileProcessService,
                           private val chatModel: ChatModel,
                            private val neonRepository: NeonRepository) {

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
             2. Provide exactly 10 brief and focused suggestions for improving the resume. Each suggestion should:
               - Be specific and actionable
               - Focus on one aspect of improvement
               - Be 1-2 sentences long
               - Start with a clear action verb
               - Be wrapped in <h3> for the title and <h5> for the details
            3. The model used for analysis (your specific model name)
            
            Format your response exactly like this:
            [match percentage]|<h3>Suggestion 1</h3><h5>Details 1</h5><h3>Suggestion 2</h3><h5>Details 2</h5>...|[model name]
            
            Example format:
            85.0|<h3>Highlight Technical Skills</h3><h5>Move your technical skills section to the top.</h5><h3>Add Project Metrics</h3><h5>Include quantifiable results for your projects.</h5>|Gemini Pro
        """.trimIndent()

        return buildAnalysedResults(chatModel.chat(analyseTemplate)).also {
            neonRepository.save<NeonModel>(NeonModel(resume=resume, analysedResults = it, userID = 1))
        }
    }

    @Tool("The analysed results are given into a class AnalysedResults ")
    fun buildAnalysedResults(
        @P("the analysed string in the format matched or not | suggestions | modelUsed") results: String): AnalysedResults {
        val splitResults = results.split("|")
        return try {
            println(splitResults)
            val suggestions = splitResults[1].trim().split("<h3>")
                .filter { it.isNotEmpty() }
                .map { it.replace("</h3>", "").replace("</h5>", "").trim() }
                .take(10)
                .toMutableList()

            while (suggestions.size < 10) {
                suggestions.add("")
            }
            
            AnalysedResults(
                match = splitResults[0].trim().toDouble(),
                suggestion1 = suggestions[0],
                suggestion2 = suggestions[1],
                suggestion3 = suggestions[2],
                suggestion4 = suggestions[3],
                suggestion5 = suggestions[4],
                suggestion6 = suggestions[5],
                suggestion7 = suggestions[6],
                suggestion8 = suggestions[7],
                suggestion9 = suggestions[8],
                suggestion10 = suggestions[9],
                modelUsed = splitResults[2].trim()
            )
        } catch (e: Exception) {
            AnalysedResults(
                match = 0.0,
                suggestion1 = "Error parsing response: ${e.message}. Original response: $results",
                modelUsed = "unknown"
            )
        }
    }
}
