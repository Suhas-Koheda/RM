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
        val analyseTemplate = "the input will be a resume and a jd . The model has to check if the $resume is a match to a $JD and return if the resume is a match to it and also it also should give any suggestions to the resume and return the match percentage and the suggestions. The model should also return the model used to analyse the resume and JD. The output should be in the format matched or not | suggestions | modelUsed."
        return buildAnalysedResults(chatModel.chat(analyseTemplate))
    }

    @Tool("The analysed results are given into a class AnalysedResults ")
    fun buildAnalysedResults(
        @P("the analysed string in the format matched or not | suggestions | modelUsed") results: String): AnalysedResults {
        val splitResults = results.split("|")
        return AnalysedResults(
            match = splitResults[0].toDouble(),
            suggestions = splitResults[1],
            modelUsed = splitResults[2]
        )
    }
}
