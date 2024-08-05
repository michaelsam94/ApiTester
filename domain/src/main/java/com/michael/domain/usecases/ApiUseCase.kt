package com.michael.domain.usecases

import com.michael.data.HttpResult
import com.michael.data.HttpStatus
import com.michael.data.repository.RequestRepository
import com.michael.domain.NetworkResult
import java.io.File

class ApiUseCase(private val repository: RequestRepository) {
    fun execute(
        method: String,
        url: String,
        headers: Map<String, String>,
        parameters: Map<String, String>,
        body: String?,
        files: Map<String, File?>?,
        onResponse: (NetworkResult)  -> Unit
    ) {
        try {
            repository.sendRequest(method, url, headers, parameters, body,files) { httpResult ->

                when(httpResult){
                    is HttpResult.Success -> {
                        onResponse.invoke(
                            NetworkResult.Success(
                                response = httpResult.response,
                                requestTime = httpResult.requestTime,
                                responseCode = httpResult.responseCode,
                                message = HttpStatus.fromCode(httpResult.responseCode)?.message ?: ""
                            )
                        )
                    }
                    is HttpResult.Error -> {
                        onResponse.invoke(
                            NetworkResult.Error(
                                httpResult.message,
                                httpResult.responseCode
                            )
                        )
                    }
                    is HttpResult.Exception -> {
                       onResponse.invoke(
                           NetworkResult.Error(
                               httpResult.exception.message ?: "",
                               -1
                           )
                       )
                    }
                }
            }

        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "", -1)
        }
    }
}