package dev.haas.rm.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import dev.haas.rm.service.ResumeProcessService
import dev.haas.rm.model.entity.AnalysedResults
import dev.haas.rm.model.dto.UploadRequest
import dev.haas.rm.model.entity.NeonModel
import java.util.logging.Logger

@RestController
@RequestMapping("/analyse")
class FileUploadController(private val resumeProcessService: ResumeProcessService) {

    private val logger = Logger.getLogger(FileUploadController::class.java.name)

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun getFileUpload(
        @RequestParam("resumeFile") resumeFile: MultipartFile,
        @RequestParam("JD") jd: String,
        @RequestParam("title") title: String,
    ): ResponseEntity<AnalysedResults> {
        try {
            if (resumeFile.isEmpty) {
                return ResponseEntity.badRequest().body(
                    AnalysedResults(
                        suggestions = "Resume file cannot be empty"
                    )
                )
            }

            val resumes = getResume()
            if (resumes.size == 5) {
                return ResponseEntity.ok(
                    AnalysedResults(
                        suggestions = "Cannot Analyse resumes further since this is in development stage. \n Thank you for trying \n Delete any resume before trying for other analysis",
                    )
                )
            }

            val uploadRequest = UploadRequest(resumeFile, jd, title)
            val result = resumeProcessService.processUploadRequest(uploadRequest)
            return ResponseEntity.ok(result)
        } catch (e: Exception) {
            logger.severe("Error in file upload: ${e.message}")
            return ResponseEntity.internalServerError().body(
                AnalysedResults(
                    suggestions = "Server error processing your request: ${e.message}"
                )
            )
        }
    }

    @GetMapping("/resume")
    fun getResume(): List<NeonModel> {
        try {
            return resumeProcessService.getResume()
        } catch (e: Exception) {
            logger.severe("Error retrieving resumes: ${e.message}")
            return emptyList()
        }
    }

    @PostMapping("/delete/{id}")
    fun deleteResume(@PathVariable id: Long): ResponseEntity<String> {
        return try {
            resumeProcessService.deleteResumeById(id)
            ResponseEntity.ok("Resume deleted successfully")
        } catch (e: Exception) {
            logger.severe("Error deleting resume: ${e.message}")
            ResponseEntity.internalServerError().body("Failed to delete resume: ${e.message}")
        }
    }
}
