package dev.haas.rm.model.dto

import org.springframework.web.multipart.MultipartFile

data class UploadRequest(
    val resumeFile: MultipartFile,
    val JD: String
)
