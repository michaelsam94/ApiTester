package com.example.domain.usecases

import com.example.data.repository.RequestRepository
import com.example.domain.SortOption
import com.example.domain.model.HttpResponse

class SortResponsesUseCase(private val repository: RequestRepository)
{
    fun execute(sortOption: SortOption?,onSort: (List<HttpResponse>) -> Unit) {
        repository.getResponses { responses ->
            val httpResponses = responses.map {
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
            val result = when (sortOption) {
                is SortOption.Ascending -> httpResponses.sortedBy { it.requestTime }
                is SortOption.Descending -> httpResponses.sortedByDescending { it.requestTime }
                is SortOption.NoSorting -> httpResponses
                else -> httpResponses
            }
            onSort.invoke(result)
        }
    }
}