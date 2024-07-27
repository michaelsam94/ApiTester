package com.example.data.remote

import com.example.data.HttpResult
import com.example.data.HttpStatus
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.util.concurrent.Executors

class ApiServiceImp: ApiService {
    override fun makeRequest(
        method: String,
        url: String,
        headers: Map<String, String>,
        parameters: Map<String, String>,
        body: String?,
        files: Map<String, File?>?, // Add files parameter
        onResponse: (HttpResult) -> Unit
    ) {
        Executors.newSingleThreadExecutor().execute {
            var connection: HttpURLConnection? = null
            val startTime = System.currentTimeMillis()
            val elapsedTime: Long
            try {
                val urlWithParams = if (parameters.isNotEmpty()) {
                    val paramString = parameters.entries.joinToString("&") {
                        "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}"
                    }
                    "$url?$paramString"
                } else {
                    url
                }

                connection = createHttpURLConnection(urlWithParams)
                connection.requestMethod = method

                // Set headers
                headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }

                if (method in listOf("POST", "PUT", "PATCH")) {
                    connection.doOutput = true
                    if (files != null && files.isNotEmpty()) {
                        // Prepare multipart request
                        val boundary = "*****" // Define a boundary for the multipart request
                        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

                        connection.outputStream.use { outputStream ->
                            // Write parameters
                            parameters.forEach { (key, value) ->
                                outputStream.write("--$boundary\r\n".toByteArray())
                                outputStream.write("Content-Disposition: form-data; name=\"$key\"\r\n\r\n".toByteArray())
                                outputStream.write("$value\r\n".toByteArray())
                            }

                            // Write files
                            files.forEach { (paramName, file) ->
                                if(file != null) {
                                    outputStream.write("--$boundary\r\n".toByteArray())
                                    outputStream.write("Content-Disposition: form-data; name=\"$paramName\"; filename=\"${file.name}\"\r\n".toByteArray())
                                    outputStream.write("Content-Type: ${URLConnection.guessContentTypeFromName(file.name)}\r\n\r\n".toByteArray())
                                    outputStream.write(file.readBytes()) // Read file bytes and write to output stream
                                    outputStream.write("\r\n".toByteArray())
                                }
                            }
                            outputStream.write("--$boundary--\r\n".toByteArray()) // End of multipart request
                        }
                    } else {
                        // Handle body or parameters if no files
                        connection.outputStream.use { outputStream ->
                            if (!body.isNullOrEmpty()) {
                                outputStream.write(body.toByteArray())
                            } else if (parameters.isNotEmpty()) {
                                val formParams = parameters.entries.joinToString("&") {
                                    "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}"
                                }
                                outputStream.write(formParams.toByteArray())
                            }
                        }
                    }
                }

                val responseCode = connection.responseCode
                val response = if (responseCode in 200..299) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                }
                elapsedTime = System.currentTimeMillis() - startTime // Calculate elapsed time
                onResponse(
                    HttpResult.Success(response,
                        HttpStatus.fromCode(responseCode)?.message ?: "", elapsedTime.toInt(), responseCode)) // Pass elapsed time

            } catch (e: FileNotFoundException) {
                onResponse(HttpResult.Error("Resource not found", 404))
            } catch (e: MalformedURLException) {
                onResponse(HttpResult.Error("Invalid URL"))
            } catch (e: ProtocolException) {
                onResponse(HttpResult.Error("Protocol error"))
            } catch (e: SocketTimeoutException) {
                onResponse(HttpResult.Error("Connection timed out"))
            } catch (e: IOException) {
                onResponse(HttpResult.Error("I/O error: ${e.message}"))
            } catch (e: Exception) {
                onResponse(HttpResult.Exception(e))
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun createHttpURLConnection(urlString: String): HttpURLConnection {
        val url = URL(urlString)
        return url.openConnection() as HttpURLConnection
    }
}