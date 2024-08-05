package com.michael.domain.usecases

import com.michael.domain.model.HttpResponse
import com.michael.data.repository.RequestRepository

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