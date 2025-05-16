package dev.haas.rm.model

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import javax.swing.Spring

@Service
class FileProcessService{

    fun processFile(resume: MultipartFile):String{
        var outputString: StringBuilder=StringBuilder()
        val iS=resume.inputStream
        val bufferedReader = BufferedReader(iS.reader())
        bufferedReader.use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                outputString.append(line)
            }
        }
        return outputString.toString()
    }

}