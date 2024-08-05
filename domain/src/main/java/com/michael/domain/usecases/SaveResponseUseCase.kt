package com.michael.domain.usecases

import com.michael.data.dto.HttpResponseDto
import com.michael.domain.model.HttpResponse
import com.michael.data.repository.RequestRepository

class SaveResponseUseCase(private val repository: RequestRepository) {

    fun execute(httpResponse: HttpResponse) {
        val dto = HttpResponseDto(
            requestUrl = httpResponse.requestUrl,
            responseCode = httpResponse.responseCode,
            error = httpResponse.error,
            headers = httpResponse.headers,
            body = httpResponse.body,
            params = httpResponse.params,
            requestTime = httpResponse.requestTime,
            requestMethod = httpResponse.requestMethod,
            requestSchema = httpResponse.requestSchema,
        )
        repository.saveResponse(dto)
    }
}