package com.michael.data.local.entity

data class HttpResponseEntity(
    val requestUrl: String,
    val requestMethod: String,
    val requestSchema: String,
    val responseCode: Int,
    val error: String?,
    val headers: String,
    val body: String,
    val params: String,
    val requestTime: Int,
)