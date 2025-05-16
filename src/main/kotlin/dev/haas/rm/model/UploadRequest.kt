package dev.haas.rm.model

import org.springframework.web.multipart.MultipartFile

data class UploadRequest(
    val resumeFile: MultipartFile,
    val JD: String
)
