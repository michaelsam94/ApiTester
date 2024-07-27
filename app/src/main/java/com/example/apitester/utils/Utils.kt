package com.example.apitester.utils

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.random.Random

fun Long.convertMillisToSecondsString(): String {
    val seconds = this / 1000
    return if (seconds == 1L) {
        "$seconds sec"
    } else {
        "$seconds secs"
    }
}

fun String.isValidUrl(): Boolean {
    val regex = Regex("^(https?://)?(www\\.)?([a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,6}(/[a-zA-Z0-9\\-._~:/?#[\\\\]@!$&'()*+,;=]*)?$")
    return regex.matches(this)
}

fun String.isValidJson(): Boolean {
    return try {
        JSONObject(this)
        true
    } catch (e: JSONException) {
        false
    }
}

fun String.parseToMap(): Map<String, String> {
    return split("&").mapNotNull {
        val parts = it.split("=")
        if (parts.size == 2) {
            parts[0].trim() to parts[1].trim()
        } else {
            null
        }
    }.toMap()
}

fun String.toMap(): Map<String, String> {
    return split("&").mapNotNull {
        val parts = it.split("=")
        if (parts.size == 2) {
            parts[0].trim() to parts[1].trim()
        } else {
            null
        }
    }.toMap()
}

fun String.toMimeType(): MimeType {
    return when (this) {
        "image/jpeg" -> MimeType.IMAGE_JPEG
        "image/png" -> MimeType.IMAGE_PNG
        "application/pdf" -> MimeType.APPLICATION_PDF
        "text/plain" -> MimeType.TEXT_PLAIN
        else -> MimeType.TEXT_PLAIN// Default or handle as needed
    }
}


fun Context.getFileFromUri(fileUri: String): File? {
    val uri = Uri.parse(fileUri)
    var file: File? = null

    // Check if the URI is a content URI
    if (uri.scheme == "content") {
        val cursor = this.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("_data")
                if (columnIndex != -1) {
                    val path = it.getString(columnIndex)
                    file = File(path)
                }
            }
        }

        // If we couldn't get the file from the cursor, we can use an InputStream
        if (file == null) {
            try {
                val inputStream = this.contentResolver.openInputStream(uri)
                val tempFile = File.createTempFile("tempFile", null, this.cacheDir)
                tempFile.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
                file = tempFile
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    } else if (uri.scheme == "file") {
        // If the URI is a file URI
        file = File(uri.path ?: return null)
    }

    return file
}

fun getRandomColor(): Color {
    return Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
}

fun formatJson(json: String): AnnotatedString {
    return try {
        val jsonObject = JSONObject(json)
        val formattedJson = jsonObject.toString(2)

        buildAnnotatedString {
            var currentColor: Color? = null

            formattedJson.forEach { char ->
                when (char) {
                    '{' -> {
                        currentColor = getRandomColor()
                        withStyle(style = SpanStyle(color = currentColor!!)) {
                            append(char.toString())
                        }
                    }
                    '}' -> {
                        currentColor?.let {
                            withStyle(style = SpanStyle(color = it)) {
                                append(char.toString())
                            }
                        } ?: append(char.toString())
                    }
                    else -> append(char.toString())
                }
            }
        }
    } catch (e: JSONException) {
        buildAnnotatedString { append(json) }
    }
}

enum class HttpMethod(val method: String) {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH");

    companion object {
        val allMethods = entries
    }
}