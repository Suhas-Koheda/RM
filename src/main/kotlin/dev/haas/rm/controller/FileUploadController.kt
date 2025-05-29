package dev.haas.rm.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import dev.haas.rm.service.ResumeProcessService
import dev.haas.rm.model.entity.AnalysedResults
import dev.haas.rm.model.dto.UploadRequest
import dev.haas.rm.model.entity.NeonModel

@RestController
@RequestMapping("/analyse")
class FileUploadController(private val resumeProcessService: ResumeProcessService) {

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun getFileUpload(
        @RequestParam("resumeFile") resumeFile: MultipartFile,
        @RequestParam("JD") jd:String,
        @RequestParam("title") title: String,
    ): ResponseEntity<AnalysedResults> {
        if(getResume().size==5){
            return ResponseEntity.ok(
                AnalysedResults(
                    suggestions = "Cannot Analyse resumes further since this is in development stage . \n Thank you for trying \n Delete any resume before trying for other analysis",
                )
            )
        }
        val uploadRequest = UploadRequest(resumeFile, jd, title)
        val result = resumeProcessService.processUploadRequest(uploadRequest)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/resume")
    fun getResume(): List<NeonModel> {
        return resumeProcessService.getResume()
    }

    @PostMapping("/delete/{id}")
    fun deleteResume(@PathVariable id: Long){
        return resumeProcessService.deleteResumeById(id)
    }
}
