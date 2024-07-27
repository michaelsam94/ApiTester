package com.example.domain.usecases

import com.example.data.dto.HttpResponseDto
import com.example.domain.model.HttpResponse
import com.example.data.repository.RequestRepository

class SaveResponseUseCase(private val repository: RequestRepository) {

    fun execute(httpResponse: HttpResponse) {
        val dto = HttpResponseDto(
            requestUrl = httpResponse.requestUrl,
            responseCode = httpResponse.responseCode,
            error = httpResponse.error,
            headers = httpResponse.headers,
            body = httpResponse.body,
            params = httpResponse.params,
            requestTime = httpResponse.requestTime
        )
        repository.saveResponse(dto)
    }
}