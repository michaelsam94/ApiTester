package com.michael.domain.usecases

import com.michael.data.repository.RequestRepository
import com.michael.domain.FilterOption
import com.michael.domain.model.HttpResponse
import com.michael.domain.model.isSuccessful

class FilterResponsesUseCase(private val repository: RequestRepository) {
    fun execute(
        requestType: FilterOption?,
        responseStatus: FilterOption?,
        onFilter: (List<HttpResponse>)-> Unit
    ) {
        repository.getResponses { responses ->
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
            }.filter { response ->
                // Initialize matches to true
                var matches = true

                // Check request type if it's not null
                if (requestType != null) {
                    matches = when (requestType) {
                        is FilterOption.Get -> response.requestMethod == FilterOption.Get.value
                        is FilterOption.Post -> response.requestMethod == FilterOption.Post.value
                        is FilterOption.Put -> response.requestMethod == FilterOption.Put.value
                        is FilterOption.Delete -> response.requestMethod == FilterOption.Delete.value
                        is FilterOption.Patch -> response.requestMethod == FilterOption.Patch.value
                        else -> true // Default case for unexpected values
                    }
                }

                // Check response status if it's not null
                if (responseStatus != null) {
                    matches = matches && when (responseStatus) {
                        is FilterOption.Success -> response.isSuccessful()
                        is FilterOption.Failure -> !response.isSuccessful()
                        else -> true // Default case for unexpected values
                    }
                }

                // Return the combined result
                matches
            }
            onFilter.invoke(result)
        }

    }
}