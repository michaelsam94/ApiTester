package com.michael.data.repository

import com.michael.data.remote.ApiService
import com.michael.data.HttpResult
import com.michael.data.dao.HttpResponseDao
import com.michael.data.dto.HttpResponseDto
import com.michael.data.local.entity.HttpResponseEntity
import java.io.File

class RequestRepositoryImpl(private val apiService: ApiService, private val dao: HttpResponseDao) :

    RequestRepository {
    override fun sendRequest(
        method: String,
        url: String,
        headers: Map<String, String>,
        parameters: Map<String, String>,
        body: String?,
        files: Map<String, File?>?,
        onResponse: (HttpResult) -> Unit
    ) {
        apiService.makeRequest(method, url, headers, parameters, body,files, onResponse)
    }

    override fun saveResponse(httpResponse: HttpResponseDto) {
        val entity = HttpResponseEntity(
            requestUrl = httpResponse.requestUrl,
            responseCode = httpResponse.responseCode,
            error = httpResponse.error,
            headers = httpResponse.headers.toString(),
            body = httpResponse.body,
            params = httpResponse.params.entries.joinToString{ "${it.key}=${it.value}"},
            requestTime = httpResponse.requestTime,
            requestSchema = httpResponse.requestSchema,
            requestMethod = httpResponse.requestMethod,
        )
        dao.insert(entity)
    }

    override fun getResponses(onResponse: (List<HttpResponseDto>) -> Unit) {
        dao.getAll { responses ->
            val result = responses.map {
                HttpResponseDto(
                    requestUrl = it.requestUrl,
                    responseCode = it.responseCode,
                    error = it.error,
                    headers = if (it.headers != "{}") it.headers.split(", ").associate {
                        val (key, value) = it.split("=")
                        key to value
                    } else emptyMap(),
                    body = it.body,
                    params = it.params.split("&").mapNotNull {
                        val parts = it.split("=")
                        if (parts.size == 2) {
                            parts[0].trim() to parts[1].trim()
                        } else {
                            null
                        }
                    }.toMap(),
                    requestTime = it.requestTime,
                    requestSchema = it.requestSchema,
                    requestMethod = it.requestMethod
                )
            }
            onResponse.invoke(result)
        }
    }
}