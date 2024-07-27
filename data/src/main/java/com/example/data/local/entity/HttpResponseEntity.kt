package com.example.data.local.entity

data class HttpResponseEntity(
    val requestUrl: String,
    val responseCode: Int,
    val error: String?,
    val headers: String,
    val body: String,
    val params: String,
    val requestTime: Int,
)