package dev.haas.rm.service

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream

@Service
public class FileProcessService {

    fun processFile(resume: MultipartFile): String {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error processing file: ${e.message}"
        }
    }

    private fun extractTextFromPDF(inputStream: InputStream): String {
        var document: PDDocument? = null
        try {
            document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            return stripper.getText(document)
        } catch (e: IOException) {
            e.printStackTrace()
            return "Error extracting text from PDF: ${e.message}"
        } finally {
            document?.close()
        }
    }
}

