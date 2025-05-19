package dev.haas.rm.model.services

import dev.haas.rm.model.AnalysedResults
import dev.haas.rm.model.UploadRequest
import org.springframework.stereotype.Service

@Service
class ResumeProcessService(
    private val fileProcessService: FileProcessService,
    private val ollamaApiService: OllamaApiService
) {

    fun processUploadRequest(uploadRequest: UploadRequest): AnalysedResults {
        val fileData = fileProcessService.processFile(uploadRequest.resumeFile)
        return analyseResume(fileData, uploadRequest.JD)
    }

    fun analyseResume(resume: String, JD: String): AnalysedResults {
        val analyseTemplate = """
            System: You are a professional recruiter with 20+ years of experience in HR. You must analyze the resume below against the job description and format your response EXACTLY as instructed.
            
            Resume:
            $resume
            
            Job Description:
            $JD
            
            Your task:
            1. Analyze how well the resume matches the job description
            2. Calculate a match percentage between 0 and 100
            3. Provide exactly 10 specific suggestions to improve the resume
            
            EXTREMELY IMPORTANT - FORMAT INSTRUCTIONS:
            - You MUST format your ENTIRE response EXACTLY as follows:
            - First: Just the match percentage number (example: 75.0)
            - Second: The pipe character |
            - Third: 10 suggestions formatted with HTML tags
            - Fourth: Another pipe character |
            - Fifth: The text "deepseek-llm" (without quotes)
            - No other text, explanations, or formatting is allowed
            
            Example of EXACT format required:
            75.0|<h3>Add Technical Skills</h3><h5>Include more specific programming languages relevant to the position.</h5><h3>Improve Resume Layout</h3><h5>Structure your resume with clear sections for better readability.</h5>|deepseek-llm
            
            Remember: Your entire response must be in this EXACT format - a number, followed by |, followed by HTML-formatted suggestions, followed by |, followed by "deepseek-llm"
        """.trimIndent()

        // Try up to 3 times to get a properly formatted response
        var attempts = 0
        var  response: String=""
        var formattedResponse: String? = null

        while (attempts < 3) {
            attempts++
            println("Attempt $attempts to get formatted response")

            response = ollamaApiService.chat(analyseTemplate)

            // Check if response has the correct format (contains at least two pipe delimiters)
            if (response.count { it == '|' } >= 2 && response.matches(Regex(".*\\d+.*\\|.*\\|.*"))) {
                formattedResponse = response
                break
            }

            // If no pipes found, try to format the response ourselves
            if (!response.contains("|")) {
                // Look for a number at the beginning
                val matchResult = Regex("(\\d+(\\.\\d+)?)").find(response)
                if (matchResult != null) {
                    val matchPercentage = matchResult.groupValues[1]
                    formattedResponse = "$matchPercentage|$response|deepseek-llm"
                    break
                }
            }

            // Wait a bit before retrying
            Thread.sleep(1000)
        }

        // If we still don't have a properly formatted response after all attempts
        if (formattedResponse == null) {
            return AnalysedResults(
                match = 0.0,
                suggestions = "Failed to get properly formatted response after $attempts attempts. Raw response: $response",
                modelUsed = "unknown"
            )
        }

        return buildAnalysedResults(formattedResponse)
    }

    fun buildAnalysedResults(results: String): AnalysedResults {
        println("Raw response from model: $results \n")
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

