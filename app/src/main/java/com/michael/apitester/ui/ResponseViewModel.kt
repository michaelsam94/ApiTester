package com.michael.apitester.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michael.apitester.utils.parseToMap
import com.michael.domain.model.HttpResponse
import com.michael.domain.model.getFullRequestUrl
import com.michael.domain.usecases.SaveResponseUseCase

class ResponseViewModel(private val saveResponseUseCase: SaveResponseUseCase) : ViewModel() {
    private val _response = MutableLiveData<String>()
    val response: LiveData<String> get() = _response

    private val _requestUrl = MutableLiveData<String>()
    val requestUrl: LiveData<String> get() = _requestUrl

    private val _responseCode = MutableLiveData<String>()
    val responseCode: LiveData<String> get() = _responseCode

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _headers = MutableLiveData<String>()
    val headers: LiveData<String> get() = _headers

    private val _body = MutableLiveData<String>()
    val body: LiveData<String> get() = _body

    private val _params = MutableLiveData<String>()
    val params: LiveData<String> get() = _params

    private val _requestTime = MutableLiveData<Int>()
    val requestTime: LiveData<Int> get() = _requestTime

    private val _requestSchema = MutableLiveData<String>()
    val requestSchema: LiveData<String> get() = _requestSchema

    private val _requestMethod = MutableLiveData<String>()
    val requestMethod: LiveData<String> get() = _requestMethod

    private val _fullRequestUrl = MutableLiveData<String>()
    val fullRequestUrl: LiveData<String> get() = _requestUrl

    fun setResponse(response: String) {
        _response.value = response
    }

    fun setRequestUrl(url: String) {
        _requestUrl.value = url
    }



    fun setResponseCode(code: String) {
        _responseCode.value = code
    }

    fun setError(error: String?) {
        _error.value = error
    }

    fun setHeaders(headers: String) {
        _headers.value = headers
    }

    fun setBody(body: String) {
        _body.value = body
    }

    fun setRequestMethod(requestMethod: String) {
        _requestMethod.value = requestMethod
    }

    fun setRequestSchema(requestSchema: String) {
        _requestSchema.value= requestSchema
    }

    fun setParams(params: String) {
        _params.value = params
    }

    fun setRequestTime(requestTime: Int) {
        _requestTime.value = requestTime
    }

    fun saveRequest(){
        val httpResponse = HttpResponse(
            responseCode = _responseCode.value?.toInt() ?: 0,
            headers = _headers.value?.parseToMap() ?: emptyMap(),
            requestMethod = _requestMethod.value ?: "GET",
            requestSchema = _requestSchema.value ?: "https://",
            requestUrl = _requestUrl.value ?: "",
            body = _body.value ?: "",
            error = _error.value ?: "",
            params = _params.value?.parseToMap() ?: emptyMap(),
            requestTime = _requestTime.value ?: 0
        )
        saveResponseUseCase.execute(httpResponse)
        _fullRequestUrl.value = httpResponse.getFullRequestUrl()
    }
}