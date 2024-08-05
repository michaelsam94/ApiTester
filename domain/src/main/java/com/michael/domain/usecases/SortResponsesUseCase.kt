package com.michael.domain.usecases

import com.michael.data.dto.HttpResponseDto
import com.michael.data.repository.RequestRepository
import com.michael.domain.SortOption
import com.michael.domain.model.HttpResponse

class SortResponsesUseCase(private val repository: RequestRepository)
{
    fun execute(sortOption: SortOption?,onSort: (List<HttpResponse>) -> Unit) {
        repository.getResponses { responses ->
            val httpResponses = responses.map {
                it.toDomainModel()
            }
            val result = when (sortOption) {
                is SortOption.Ascending -> httpResponses.sortedBy { it.requestTime }
                is SortOption.Descending -> httpResponses.sortedByDescending { it.requestTime }
                is SortOption.NoSorting -> httpResponses
                else -> httpResponses
            }
            onSort.invoke(result)
        }
    }

    private fun HttpResponseDto.toDomainModel() = HttpResponse(
        requestUrl = requestUrl,
        responseCode = responseCode,
        error = error,
        headers = headers,
        body = body,
        params = params,
        requestTime = requestTime,
        requestSchema = requestSchema,
        requestMethod = requestMethod
    )
}