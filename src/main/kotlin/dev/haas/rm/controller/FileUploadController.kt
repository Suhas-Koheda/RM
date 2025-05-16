package dev.haas.rm.controller

import dev.haas.rm.model.AnalysedResults
import dev.haas.rm.model.ResumeProcessService
import dev.haas.rm.model.UploadRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/analyse")
class FileUploadController(private val resumeProcessService: ResumeProcessService){

    @PostMapping("/upload")
    fun getFileUpload(@RequestBody uploadRequest: UploadRequest): ResponseEntity<AnalysedResults> {
        val result = resumeProcessService.processUploadRequest(uploadRequest)
        return ResponseEntity.ok(result)
    }
}
