package com.example.apitester.di

import android.content.Context
import com.example.data.remote.ApiService
import com.example.data.dao.HttpResponseDao
import com.example.data.local.AppDatabase
import com.example.data.remote.ApiServiceImp
import com.example.data.repository.RequestRepository
import com.example.data.repository.RequestRepositoryImpl
import com.example.domain.usecases.ApiUseCase
import com.example.domain.usecases.FilterResponsesUseCase
import com.example.domain.usecases.GetResponsesUseCase
import com.example.domain.usecases.SaveResponseUseCase
import com.example.domain.usecases.SortResponsesUseCase


object ServiceLocator {

    private var apiService: ApiService = ApiServiceImp()

    private var database: AppDatabase? = null

    @Volatile
    var requestRepository: RequestRepository? = null

    private lateinit var apiUseCase: ApiUseCase
    private lateinit var saveResponseUseCase: SaveResponseUseCase
    private lateinit var getResponseUseCase: GetResponsesUseCase
    private lateinit var filterResponsesUseCase: FilterResponsesUseCase
    private lateinit var sortResponsesUseCase: SortResponsesUseCase


    fun initDB(context: Context) {
        synchronized(this) {
            requestRepository = requestRepository ?: createRequestRepository(context)
            requestRepository.let {
                apiUseCase = ApiUseCase(requestRepository!!)
                saveResponseUseCase = SaveResponseUseCase(requestRepository!!)
                getResponseUseCase = GetResponsesUseCase(requestRepository!!)
                filterResponsesUseCase = FilterResponsesUseCase(requestRepository!!)
                sortResponsesUseCase = SortResponsesUseCase(requestRepository!!)
            }

        }
    }

    private fun createRequestRepository(context: Context): RequestRepository {
        val database = database ?: createDatabase(context)
        val repository = RequestRepositoryImpl(apiService, HttpResponseDao(database))
        requestRepository = repository
        return repository
    }

    private fun createDatabase(context: Context): AppDatabase {
        val result = AppDatabase.getDatabase(context)
        database = result
        return result
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