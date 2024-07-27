package com.example.domain

sealed class NetworkResult {
    data class Success(val response: String,val message: String = "",val requestTime: Int, val responseCode: Int) : NetworkResult()
    data class Error(val message: String, val responseCode: Int = 0) : NetworkResult()
}