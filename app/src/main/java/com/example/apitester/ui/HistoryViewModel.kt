package com.example.apitester.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.model.HttpResponse
import com.example.domain.usecases.GetResponsesUseCase

class HistoryViewModel(private val getResponsesUseCase: GetResponsesUseCase) : ViewModel() {
    private val _historyItems = MutableLiveData<List<HttpResponse>>()
    val historyItems: LiveData<List<HttpResponse>> get() = _historyItems

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing
    init {
        loadHistory()
    }

    private fun loadHistory() {
        getResponsesUseCase.execute { responses ->
            Log.d("HistoryViewModel",responses.toString())
            _historyItems.postValue(responses)// Update LiveData with the new responses
        }

    }
}