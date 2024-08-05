package com.michael.apitester.utils

enum class MimeType(val type: String) {
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    APPLICATION_PDF("application/pdf"),
    TEXT_PLAIN("text/plain");
    // Add more MIME types as needed

    companion object {
        val allMimeTypes = entries // Returns all enum constants as a list
    }
}