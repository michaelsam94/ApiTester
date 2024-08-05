package com.michael.data

sealed class HttpResult {
    data class Success(val response: String,val message: String,val requestTime: Int, val responseCode: Int) : HttpResult()
    data class Error(val message: String, val responseCode: Int = 0) : HttpResult()
    data class Exception(val exception: Throwable) : HttpResult()
}