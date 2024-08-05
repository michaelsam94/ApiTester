package com.michael.data.repository

import com.michael.data.HttpResult
import com.michael.data.dto.HttpResponseDto
import java.io.File

interface RequestRepository {

    fun sendRequest(method: String, url: String, headers: Map<String, String>, parameters: Map<String, String>, body: String?, files: Map<String, File?>?,onResponse: (HttpResult)  -> Unit)
    fun saveResponse(httpResponse: HttpResponseDto)
    fun getResponses(onResponse: (List<HttpResponseDto>) -> Unit)
}