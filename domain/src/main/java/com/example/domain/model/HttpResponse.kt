package com.example.domain.model

data class HttpResponse(
    val requestUrl: String,
    val responseCode: Int,
    val error: String?,
    val headers: Map<String, String>,
    val body: String,
    val params: Map<String, String>,
    val requestTime: Int,
)