package com.example.apitester.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apitester.utils.HttpMethod
import com.example.domain.usecases.ApiUseCase
import com.example.domain.NetworkResult

class HomeViewModel(private val apiUseCase: ApiUseCase) : ViewModel() {
    private val _selectedMethod:MutableLiveData<HttpMethod> = MutableLiveData(HttpMethod.GET)
    val selectedMethod: LiveData<HttpMethod> get() = _selectedMethod

    private val _selectedSchema = MutableLiveData("https://")
    val selectedSchema: LiveData<String> get() = _selectedSchema

    private val _url = MutableLiveData("dummyjson.com/auth/login")
    val url: LiveData<String> get() = _url

    private val _body = MutableLiveData("{ \"username\": \"emilys\",\"password\": \"emilyspass\",\"expiresInMins\": 30}")
    val body: LiveData<String> get() = _body

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _headers = MutableLiveData<SnapshotStateList<Pair<String, String>>>()
    val headers: LiveData<SnapshotStateList<Pair<String, String>>> get() = _headers

    private val _parameters = MutableLiveData<SnapshotStateList<Pair<String, String>>>()
    val parameters: LiveData<SnapshotStateList<Pair<String, String>>> get() = _parameters

    private val _response = MutableLiveData<String>()
    val response: LiveData<String> get() = _response

    init {
        _headers.value = SnapshotStateList()
        _parameters.value = SnapshotStateList()
    }

    fun updateSelectedMethod(method: HttpMethod) {
        _selectedMethod.value = method
    }

    fun updateSelectedSchema(schema: String) {
        _selectedSchema.value = schema
    }

    fun updateUrl(url: String) {
        _url.value = url
    }

    fun updateBody(body: String) {
        _body.value = body
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun addHeader(header: Pair<String, String>) {
        _headers.value?.add(header)
    }

    fun removeHeader(index: Int) {
        _headers.value?.removeAt(index)
    }

    fun updateHeader(index: Int, header: Pair<String, String>) {
        _headers.value?.set(index, header)
    }

    fun addParameter(parameter: Pair<String, String>) {
        _parameters.value?.add(parameter)
    }

    fun removeParameter(index: Int) {
        _parameters.value?.removeAt(index)
    }

    fun updateParameter(index: Int, parameter: Pair<String, String>) {
        _parameters.value?.set(index, parameter)
    }

    fun makeRequest(
        method: String,
        url: String,
        headers: Map<String, String>,
        parameters: Map<String, String>,
        body: String?,
        onResult: (NetworkResult) -> Unit // Add a callback for the result
    ) {


        val requestUrl = "${selectedSchema.value}$url"
        apiUseCase.execute(method, requestUrl, headers, parameters, body) { response ->
            onResult.invoke(response)
        }


    }
}