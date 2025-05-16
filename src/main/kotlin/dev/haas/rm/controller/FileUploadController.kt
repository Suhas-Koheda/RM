package dev.haas.rm.controller

import dev.haas.rm.model.AnalysedResults
import dev.haas.rm.model.ResumeProcessService
import dev.haas.rm.model.UploadRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/analyse")
class FileUploadController(private val resumeProcessService: ResumeProcessService) {

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun getFileUpload(
        @RequestParam("resumeFile") resumeFile: MultipartFile,
        @RequestParam("JD") jd: String
    ): ResponseEntity<AnalysedResults> {
        val uploadRequest = UploadRequest(resumeFile, jd)
        val result = resumeProcessService.processUploadRequest(uploadRequest)
        return ResponseEntity.ok(result)
    }
}
