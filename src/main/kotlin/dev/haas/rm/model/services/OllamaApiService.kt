package dev.haas.rm.model.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

@Service
class OllamaApiService {

    @Value("\${ollama.base.url}")
    private lateinit var baseUrl: String

    fun chat(prompt: String, modelName: String = "deepseek-r1:7b"): String {
        println("Sending request to Ollama API: $baseUrl")

        try {
            val response = makeRequest("$baseUrl/api/generate", prompt, modelName)
            if (response.isNotBlank() && !response.startsWith("Error")) {
                return response
            }
        } catch (e: Exception) {
            println("Error with /api/generate: ${e.message}")
        }
        println("Trying fallback to /api/chat endpoint...")
        try {
            return makeRequest("$baseUrl/api/chat", prompt, modelName)
        } catch (e: Exception) {
            println("Error with /api/chat: ${e.message}")
            e.printStackTrace()
            return "Error calling Ollama API: ${e.message}"
        }
    }

    private fun makeRequest(endpoint: String, prompt: String, modelName: String): String {
        val url = URL(endpoint)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        val escapedPrompt = escapeJsonString(prompt)
        val jsonPayload = """{"model":"$modelName","prompt":"$escapedPrompt","stream":false}"""

        val writer = OutputStreamWriter(connection.outputStream)
        writer.write(jsonPayload)
        writer.flush()

        val responseCode = connection.responseCode
        println("Response Code from $endpoint: $responseCode")

        val reader = BufferedReader(InputStreamReader(
            if (responseCode >= 200 && responseCode < 300) connection.inputStream
            else connection.errorStream
        ))

        val response = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }

        reader.close()
        connection.disconnect()

        val responseBody = response.toString()
        println("Raw API response from $endpoint: $responseBody")
        return if (responseBody.contains("\"response\":")) {
            responseBody.substringAfter("\"response\":\"").substringBefore("\",")
                .replace("\\\\n", "\n")  // Handle double-escaped newlines in the JSON response
                .replace("\\n", "\n")     // Handle regular escaped newlines
        } else if (responseBody.contains("\"message\":") && responseBody.contains("\"content\":")) {
            responseBody.substringAfter("\"content\":\"").substringBefore("\",")
                .replace("\\\\n", "\n")  // Handle double-escaped newlines
                .replace("\\n", "\n")    // Handle regular escaped newlines
        } else {
            responseBody
        }
    }
    private fun escapeJsonString(str: String): String {
        return str.replace("\\", "\\\\")  // Replace backslash with double backslash
                .replace("\"", "\\\"")    // Replace quote with escaped quote
                .replace("\b", "\\b")     // Replace backspace with escaped backspace
                .replace("\n", "\\n")     // Replace newline with escaped newline
                .replace("\r", "\\r")     // Replace carriage return with escaped carriage return
                .replace("\t", "\\t")     // Replace tab with escaped tab
    }
}
