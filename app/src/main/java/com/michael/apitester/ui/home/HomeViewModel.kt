package com.michael.apitester.ui.home

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michael.apitester.utils.HttpMethod
import com.michael.domain.NetworkResult
import com.michael.domain.usecases.ApiUseCase
import com.google.ai.client.generativeai.common.shared.FileData
import java.io.File

class HomeViewModel(private val apiUseCase: ApiUseCase) : ViewModel() {

    private val _selectedMethod = MutableLiveData<HttpMethod>(HttpMethod.GET)
    val selectedMethod: LiveData<HttpMethod> = _selectedMethod

    private val _selectedSchema = MutableLiveData("https://")
    val selectedSchema: LiveData<String> = _selectedSchema

    private val _url = MutableLiveData("")
    val url: LiveData<String> = _url

    private val _body = MutableLiveData("")
    val body: LiveData<String> = _body

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _headers = MutableLiveData(SnapshotStateList<Pair<String, String>>())
    val headers: LiveData<SnapshotStateList<Pair<String, String>>> = _headers

    private val _parameters = MutableLiveData(SnapshotStateList<Pair<String, String>>())
    val parameters: LiveData<SnapshotStateList<Pair<String, String>>> = _parameters

    private val _response = MutableLiveData("")
    val response: LiveData<String> = _response

    private val _fileNames = MutableLiveData(arrayListOf<String>())
    val fileNames: LiveData<ArrayList<String>> = _fileNames

    private val _fileUris = MutableLiveData(arrayListOf<Uri>())
    val fileUri: LiveData<ArrayList<Uri>> = _fileUris

    fun addFileName(name: String) {
        _fileNames.value?.add(name)
    }

    fun updateFileNameAtIndex(name: String,index: Int){
        if(_fileNames.value != null && _fileNames.value!!.size > 0){
            _fileNames.value!![index] = name
        }
    }

    fun removeLastFileName(){
        if(_fileNames.value != null && _fileNames.value!!.size > 0){
            _fileNames.value!!.removeAt(_fileNames.value!!.lastIndex)
        }
    }


    fun addFileUrl(fileUri: Uri) {
        _fileUris.value?.add(fileUri)
    }

    fun updateFileUriAtIndex(fileUri: Uri,index: Int){
        if(_fileUris.value != null && _fileUris.value!!.size > 0){
            _fileUris.value!![index] = fileUri
        }
    }

    fun removeLastFileUri(){
        if(_fileUris.value != null && _fileUris.value!!.size > 0){
            _fileUris.value!!.removeAt(_fileUris.value!!.lastIndex)
        }
    }

    private val _files = MutableLiveData(mutableStateListOf<FileData>())
    val files: LiveData<SnapshotStateList<FileData>> = _files

    fun updateSelectedMethod(method: HttpMethod) {
        _selectedMethod.value = method
    }

    fun updateSelectedSchema(schema: String) {
        _selectedSchema.value = schema
    }

    fun updateUrl(newUrl: String) {
        _url.value = newUrl
    }

    fun updateBody(newBody: String) {
        _body.value = newBody
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun addHeader(header: Pair<String, String>) {
        _headers.value?.add(header)
    }

    fun removeLastHeader() {
        if(_headers.value != null && _headers.value?.size!! > 0) {
            _headers.value?.removeAt( _headers.value?.size!! - 1)
        }

    }

    fun updateHeader(index: Int, header: Pair<String, String>) {
        _headers.value?.set(index, header)
    }

    fun addParameter(parameter: Pair<String, String>) {
        _parameters.value?.add(parameter)
    }

    fun removeLastParameter() {
        if(_parameters.value != null && _parameters.value?.size!! > 0) {
            _parameters.value?.removeAt(_parameters.value?.size!! - 1)
        }

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
        files: Map<String, File?>?,
        onResult: (NetworkResult) -> Unit
    ) {
        val requestUrl = "${selectedSchema.value}$url"
        apiUseCase.execute(method, requestUrl, headers, parameters, body,files) { response ->
            onResult.invoke(response)
        }
    }

    fun addHeader() {
        _headers.value?.add(Pair("", ""))
    }

    fun updateHeader(index: Int, key: String, value: String) {
        _headers.value?.set(index, Pair(key, value))
    }

    fun removeHeader(index: Int) {
        _headers.value?.removeAt(index)
    }

    fun addParameter() {
        _parameters.value?.add(Pair("", ""))
    }

    fun updateParameter(index: Int, key: String, value: String) {
        _parameters.value?.set(index, Pair(key, value))
    }

    fun removeParameter(index: Int) {
        _parameters.value?.removeAt(index)
    }

    fun addFile() {
        _files.value?.add(FileData("", ""))
    }

    fun updateFile(index: Int, uri: String, mimeType: String) {
        _files.value?.set(index, FileData(uri, mimeType))
    }

    fun updateFileType(index: Int, mimeType: String) {
        _files.value?.let { fileList ->
            fileList[index] = fileList[index].copy(mimeType = mimeType)
            _files.value = fileList
        }
    }


    fun removeLastFile() {
        if (_files.value?.isNotEmpty() == true) {
            _files.value?.removeAt(_files.value?.size?.minus(1) ?: 0)
        }

    }

}