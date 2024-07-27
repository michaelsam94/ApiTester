package com.example.data.repository

import com.example.data.HttpResult
import com.example.data.dto.HttpResponseDto

interface RequestRepository {

    fun sendRequest(method: String, url: String, headers: Map<String, String>, parameters: Map<String, String>, body: String?,onResponse: (HttpResult)  -> Unit)
    fun saveResponse(httpResponse: HttpResponseDto)
    fun getResponses(onResponse: (List<HttpResponseDto>) -> Unit)
}