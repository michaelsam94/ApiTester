package com.michael.apitester.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.michael.apitester.di.ServiceLocator
import com.michael.apitester.ui.home.HomeViewModel

class ViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(ServiceLocator.getApiUseCase()) as T
        } else if (modelClass.isAssignableFrom(ResponseViewModel::class.java)) {
            return ResponseViewModel(ServiceLocator.getSaveResponseUseCase()) as T
        } else if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(
                ServiceLocator.getResponsesUseCase(),
                ServiceLocator.getSortResponsesUseCase(),
                ServiceLocator.getFilterResponsesUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}