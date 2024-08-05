package com.michael.apitester.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michael.domain.FilterOption
import com.michael.domain.SortOption
import com.michael.domain.model.HttpResponse
import com.michael.domain.usecases.FilterResponsesUseCase
import com.michael.domain.usecases.GetResponsesUseCase
import com.michael.domain.usecases.SortResponsesUseCase

class HistoryViewModel(
    private val getResponsesUseCase: GetResponsesUseCase,
    private val sortResponsesUseCase: SortResponsesUseCase,
    private val filterResponsesUseCase: FilterResponsesUseCase
) : ViewModel() {
    private val _historyItems = MutableLiveData<List<HttpResponse>>()
    val historyItems: LiveData<List<HttpResponse>> get() = _historyItems

    private val _moreMenuExpanded = MutableLiveData<Boolean>()
    val moreMenuExpanded: LiveData<Boolean> get() = _moreMenuExpanded

    private val _showSortDialog = MutableLiveData<Boolean>()
    val showSortDialog: LiveData<Boolean> get() = _showSortDialog

    private val _showFilterTypeDialog = MutableLiveData<Boolean>()
    val showFilterTypeDialog: LiveData<Boolean> get() = _showFilterTypeDialog

    private val _showFilterStatusDialog = MutableLiveData<Boolean>()
    val showFilterStatusDialog: LiveData<Boolean> get() = _showFilterStatusDialog

    private val _selectedSortOption = MutableLiveData<SortOption?>(null)
    val selectedSortOption: LiveData<SortOption?> get() = _selectedSortOption

    private val _selectedRequestType = MutableLiveData<FilterOption?>(null)
    val selectedRequestType: LiveData<FilterOption?> get() = _selectedRequestType

    private val _selectedResponseStatus = MutableLiveData<FilterOption?>(null)
    val selectedResponseStatus: LiveData<FilterOption?> get() = _selectedResponseStatus

    init {
        loadHistory()
    }

    private fun loadHistory() {
        getResponsesUseCase.execute { responses ->
            _historyItems.postValue(responses)
        }
    }

    fun setMoreExpanded(isExpanded: Boolean) {
        _moreMenuExpanded.value = isExpanded
    }

    fun showSortDialog(show: Boolean) {
        _showSortDialog.value = show
    }

    fun showFilterTypeDialog(show: Boolean) {
        _showFilterTypeDialog.value = show
    }

    fun showFilterStatusDialog(show: Boolean) {
        _showFilterStatusDialog.value = show
    }

    fun selectSortOption(option: SortOption?) {
        _selectedSortOption.value = option
        resetFilterStates() // Reset other filters when a sort option is selected

    }

    fun selectRequestType(type: FilterOption?) {
        _selectedRequestType.value = type
        resetSortAndStatusStates() // Reset sort and status when a request type is selected
    }

    fun selectResponseStatus(status: FilterOption?) {
        _selectedResponseStatus.value = status
        resetSortAndTypeStates() // Reset sort and type when a response status is selected
    }

    private fun resetSortAndStatusStates() {
        _selectedSortOption.value = null
        _selectedResponseStatus.value = null
    }

    private fun resetFilterStates() {
        _selectedRequestType.value = null
        _selectedResponseStatus.value = null
    }

    private fun resetSortAndTypeStates() {
        _selectedSortOption.value = null
        _selectedRequestType.value = null
    }

    fun applySortingAndFiltering() {
        if(_selectedSortOption.value != null) {
             sortResponsesUseCase.execute(_selectedSortOption.value) { result ->
                 _historyItems.postValue(result)
             }
        }
        if(_selectedRequestType.value != null) {
            filterResponsesUseCase.execute(responseStatus = null, requestType = _selectedRequestType.value ?: FilterOption.NoFilter) { result ->
                _historyItems.postValue(result)
            }
        }
        if(_selectedResponseStatus.value != null) {
            filterResponsesUseCase.execute(responseStatus = _selectedResponseStatus.value ?: FilterOption.NoFilter, requestType = null) { result ->
                _historyItems.postValue(result)
            }
        }
    }
}
