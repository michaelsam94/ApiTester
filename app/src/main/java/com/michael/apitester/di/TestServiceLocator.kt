package com.michael.apitester.di

import com.michael.data.remote.ApiService
import com.michael.data.repository.RequestRepository
import com.michael.domain.usecases.ApiUseCase
import com.michael.domain.usecases.FilterResponsesUseCase
import com.michael.domain.usecases.GetResponsesUseCase
import com.michael.domain.usecases.SaveResponseUseCase
import com.michael.domain.usecases.SortResponsesUseCase

object TestServiceLocator {

    private lateinit var apiService: ApiService
    private lateinit var requestRepository: RequestRepository

    private lateinit var apiUseCase: ApiUseCase
    private lateinit var saveResponseUseCase: SaveResponseUseCase
    private lateinit var getResponseUseCase: GetResponsesUseCase
    private lateinit var filterResponsesUseCase: FilterResponsesUseCase
    private lateinit var sortResponsesUseCase: SortResponsesUseCase

    fun initTest(apiService: ApiService, repository: RequestRepository) {
        this.apiService = apiService
        this.requestRepository = repository

        apiUseCase = ApiUseCase(requestRepository)
        saveResponseUseCase = SaveResponseUseCase(requestRepository)
        getResponseUseCase = GetResponsesUseCase(requestRepository)
        filterResponsesUseCase = FilterResponsesUseCase(requestRepository)
        sortResponsesUseCase = SortResponsesUseCase(requestRepository)
    }

    fun getApiUseCase(): ApiUseCase {
        return apiUseCase
    }

    fun getSaveResponseUseCase(): SaveResponseUseCase {
        return saveResponseUseCase
    }

    fun getResponseUseCase(): GetResponsesUseCase {
        return getResponseUseCase
    }

    fun getFilterResponsesUseCase(): FilterResponsesUseCase {
        return filterResponsesUseCase
    }

    fun getSortResponsesUseCase(): SortResponsesUseCase {
        return sortResponsesUseCase
    }

}
