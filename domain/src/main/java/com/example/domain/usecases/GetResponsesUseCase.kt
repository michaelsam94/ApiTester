package com.example.domain.usecases

import com.example.domain.model.HttpResponse
import com.example.data.repository.RequestRepository

class GetResponsesUseCase(private val repository: RequestRepository) {

    fun execute(onResponse: (List<HttpResponse>) -> Unit) {
        repository.getResponses() { responses ->
            run {
                val result = responses.map {
                    HttpResponse(
                        requestUrl = it.requestUrl,
                        responseCode = it.responseCode,
                        error = it.error,
                        headers = it.headers,
                        body = it.body,
                        params = it.params,
                        requestTime = it.requestTime,
                        requestSchema = it.requestSchema,
                        requestMethod = it.requestMethod,
                    )
                }
                onResponse.invoke(result)
            }

        }
    }
}