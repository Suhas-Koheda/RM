package dev.haas.rm.service

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStream

@Service
class FileProcessService {

    fun processFile(resume: MultipartFile): String {
        if (resume.originalFilename?.endsWith(".pdf", ignoreCase = true) == true) {
            return extractTextFromPDF(resume.inputStream)
        } else {
            val outputString = StringBuilder()
            val bufferedReader = BufferedReader(resume.inputStream.reader())
            bufferedReader.use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    outputString.append(line).append("\n")
                }
            }
            return outputString.toString()
        }
    }

    private fun extractTextFromPDF(inputStream: InputStream): String {
        val document = PDDocument.load(inputStream)
        try {
            val stripper = PDFTextStripper()
            return stripper.getText(document)
        } finally {
            document.close()
        }
    }
}