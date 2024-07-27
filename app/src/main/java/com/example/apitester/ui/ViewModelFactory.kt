package com.example.apitester.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apitester.di.ServiceLocator
import com.example.apitester.ui.home.HomeViewModel

class ViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(ServiceLocator.getApiUseCase()) as T
        } else if (modelClass.isAssignableFrom(ResponseViewModel::class.java)) {
            return ResponseViewModel(ServiceLocator.getSaveResponseUseCase()) as T
        } else if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(
                ServiceLocator.getResponseUseCase(),
                ServiceLocator.getSortResponsesUseCase(),
                ServiceLocator.getFilterResponsesUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}