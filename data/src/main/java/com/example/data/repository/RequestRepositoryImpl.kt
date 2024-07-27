package com.example.data.repository

import com.example.data.ApiService
import com.example.data.HttpResult
import com.example.data.dao.HttpResponseDao
import com.example.data.dto.HttpResponseDto
import com.example.data.local.entity.HttpResponseEntity

class RequestRepositoryImpl(private val apiService: ApiService, private val dao: HttpResponseDao) :

    RequestRepository {
    override fun sendRequest(
        method: String,
        url: String,
        headers: Map<String, String>,
        parameters: Map<String, String>,
        body: String?,
        onResponse: (HttpResult) -> Unit
    ) {
        apiService.makeRequest(method, url, headers, parameters, body, onResponse)
    }

    override fun saveResponse(httpResponse: HttpResponseDto) {
        val entity = HttpResponseEntity(
            requestUrl = httpResponse.requestUrl,
            responseCode = httpResponse.responseCode,
            error = httpResponse.error,
            headers = httpResponse.headers.toString(),
            body = httpResponse.body,
            params = httpResponse.params.toString(),
            requestTime = httpResponse.requestTime
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
                    params = if(it.params != "{}") {
                        it.params.split(", ").associate {
                            val (key, value) = it.split("=")
                            key to value
                        }
                    } else {
                      emptyMap()
                    },
                    requestTime = it.requestTime
                )
            }
            onResponse.invoke(result)
        }
    }
}