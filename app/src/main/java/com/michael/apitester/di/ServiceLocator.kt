package com.michael.apitester.di

import android.content.Context
import com.michael.data.remote.ApiService
import com.michael.data.dao.HttpResponseDao
import com.michael.data.local.AppDatabase
import com.michael.data.remote.ApiServiceImp
import com.michael.data.repository.RequestRepository
import com.michael.data.repository.RequestRepositoryImpl
import com.michael.domain.usecases.*

object ServiceLocator {

    // Instance variables
    private var apiService: ApiService = ApiServiceImp()
    private var database: AppDatabase? = null

    @Volatile
    private var requestRepository: RequestRepository? = null

    // Use cases
    private lateinit var apiUseCase: ApiUseCase
    private lateinit var saveResponseUseCase: SaveResponseUseCase
    private lateinit var getResponsesUseCase: GetResponsesUseCase
    private lateinit var filterResponsesUseCase: FilterResponsesUseCase
    private lateinit var sortResponsesUseCase: SortResponsesUseCase

    // Initialize database and use cases
    fun initDB(context: Context) {
        synchronized(this) {
            if (requestRepository == null) {
                requestRepository = createRequestRepository(context)
            }

            requestRepository?.let {
                apiUseCase = ApiUseCase(it)
                saveResponseUseCase = SaveResponseUseCase(it)
                getResponsesUseCase = GetResponsesUseCase(it)
                filterResponsesUseCase = FilterResponsesUseCase(it)
                sortResponsesUseCase = SortResponsesUseCase(it)
            }
        }
    }

    // Create Request Repository
    private fun createRequestRepository(context: Context): RequestRepository {
        val db = database ?: createDatabase(context)
        return RequestRepositoryImpl(apiService, HttpResponseDao(db)).also {
            requestRepository = it
        }
    }

    // Create Database
    private fun createDatabase(context: Context): AppDatabase {
        return AppDatabase.getDatabase(context).also {
            database = it
        }
    }

    // Getters for use cases
    fun getApiUseCase(): ApiUseCase = apiUseCase

    fun getSaveResponseUseCase(): SaveResponseUseCase = saveResponseUseCase

    fun getResponsesUseCase(): GetResponsesUseCase = getResponsesUseCase

    fun getFilterResponsesUseCase(): FilterResponsesUseCase = filterResponsesUseCase

    fun getSortResponsesUseCase(): SortResponsesUseCase = sortResponsesUseCase
}
