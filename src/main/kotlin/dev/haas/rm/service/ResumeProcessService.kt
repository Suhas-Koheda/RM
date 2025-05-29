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
            4. Give me the resume in proper overleaf format like below
                ---format 
               \documentclass[a4paper,10pt]{article}

               \usepackage{geometry}
               \geometry{left=0.75in, right=0.75in, top=0.75in, bottom=0.5in}
               \usepackage{enumitem}
               \usepackage{hyperref}
               \usepackage{titlesec}
               \usepackage{parskip}
               \usepackage{multicol}
               \usepackage{sectsty}
               \usepackage{ragged2e}

               % Define a custom section format with an underline
               \titleformat{\section}{
                   \large\bfseries}{}{0em}{\titlerule[0.5pt]\vspace{0.5ex}}
               \titleformat{\subsection}{
                   \bfseries}{}{0em}{\titlerule[0.5pt]\vspace{0.5ex}}

               % Compact spacing
               \setlength{\parskip}{0.4em}
               \setlength{\parindent}{0em}

               % Add custom styles to sections
               \sectionfont{\uppercase}
               \subsectionfont{\underline}

               \begin{document}

               \begin{center}
                   \textbf{\LARGE Your Name}\\
                   \href{mailto:your-email@example.com}{your-email@example.com} \hspace{10pt} | \hspace{10pt} +1-234-567-8901 \hspace{10pt} | \hspace{10pt} \href{https://github.com/your-username}{github.com/your-username} \hspace{10pt} | \hspace{10pt} \href{https://your-portfolio.com}{Portfolio} \hspace{10pt} | \hspace{10pt} \href{https://linkedin.com/in/your-profile}{LinkedIn}
               \end{center}

               \section*{Education}
               \hline
               \textbf{University Name}, City, Country \hfill Year – Year\\
               Degree Name — CGPA: X.X

               \textbf{School Name}, City \hfill Year – Year\\
               Board/Curriculum — Score: XX\%

               \section*{Skills}
               \hline
               Skill 1 — Skill 2 — Skill 3 — Skill 4 — Skill 5 — Skill 6 — Skill 7 — Skill 8 — Skill 9 — Skill 10    

               \section*{Open Source Contributions}
               \hline
               \textbf{Project Name} \hfill \textit{Technologies Used} \hfill Month Year\\
               Brief description of contribution.

               \hfill
               \begin{flushright}
                   \href{https://github.com/repo-link}{Code} \hspace{10pt} | \hspace{10pt} \href{https://live-demo.com}{Live Demo} (if available)
               \end{flushright}

               \section*{Projects}
               \hline
               \hfill
               \textbf{Project Name} \hfill \textit{Technologies Used} \hfill Month Year\\
               Description of project including key features and technologies.

               \begin{flushright}
                   \href{https://github.com/repo-link}{Code} \hspace{10pt} | \hspace{10pt} \href{https://live-demo.com}{Live Demo} (if available)
               \end{flushright}

               \section*{Work Experience}
               \hline
               \textbf{Company Name} — \textit{Position} \hfill Month Year – Present/Month Year\\
               Description of responsibilities and achievements.

               \section*{Positions of Responsibility}
               \hline
               \textbf{Organization Name} — \textit{Position (Month Year - Month Year), Additional Roles (Month Year - Month Year)}\\
               Description of responsibilities and contributions.

               \end{document}
                format ---
             
            Format your response exactly like this:
            [match percentage]|<h3>Suggestion 1</h3><h5>Details 1</h5><h3>Suggestion 2</h3><h5>Details 2</h5>...|[model name]|Resume in the overleaf format 
        """.trimIndent()

        return buildAnalysedResults(chatModel.chat(analyseTemplate),JD).also {
            neonRepository.save(NeonModel(
                analysedResults = it,
                userID = SecurityContextHolder.getContext().authentication.principal as Long,
                title = title
            ))
        }
    }

    @Tool("The analysed results are given into a class Analysed Results ")
    fun buildAnalysedResults(
        @P("the analysed string in the format -> matched or not | suggestions | modelUsed") results: String,jD:String): AnalysedResults {
        val splitResults = results.split("|")
        return try {
            println(splitResults)
            AnalysedResults(
                match = splitResults[0].trim().toDouble(),
                suggestions = splitResults[1].trim(),
                modelUsed = splitResults[2].trim(),
                jD=jD,
                resume = splitResults[3].trim()
            )
        } catch (e: Exception) {
            AnalysedResults(
                match = 0.0,
                suggestions = "Error parsing response: ${e.message}. Original response: $results",
                modelUsed = "unknown",
                jD=jD,
                resume = ""
            )
        }
    }

    fun getResume(): List<NeonModel> {
        return neonRepository.findAllByUserID(SecurityContextHolder.getContext().authentication.principal as Long).also { println(SecurityContextHolder.getContext().authentication.principal as Long) }
    }

    fun deleteResumeById(id:Long){
        return neonRepository.deleteById(id)
    }
}
