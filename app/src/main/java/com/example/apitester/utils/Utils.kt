package com.example.apitester.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.json.JSONException
import org.json.JSONObject
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