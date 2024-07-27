package com.example.apitester.usecases

import com.example.apitester.di.TestServiceLocator
import com.example.data.HttpResult
import com.example.data.remote.ApiService
import com.example.data.repository.RequestRepository
import com.example.domain.NetworkResult
import com.example.domain.usecases.ApiUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.io.File

class ApiUseCaseTest {

    private lateinit var apiService: ApiService
    private lateinit var requestRepository: RequestRepository
    private lateinit var apiUseCase: ApiUseCase

    @Before
    fun setup() {
        apiService = mockk() // Create a mock for the ApiService
        requestRepository = mockk() // Create a mock for the RequestRepository

        // Initialize the test service locator
        TestServiceLocator.initTest(apiService, requestRepository)
        apiUseCase = TestServiceLocator.getApiUseCase()
    }

    @Test
    fun `test successful API request`() {
        // Arrange
        val method = "GET"
        val url = "https://example.com/api"
        val headers = mapOf("Authorization" to "Bearer token")
        val parameters = emptyMap<String, String>()
        val body: String? = null
        val files: Map<String, File?>? = null

        every { requestRepository.sendRequest(method, url, headers, parameters, body, files, any()) } answers {
            val callback = it.invocation.args[6] as (HttpResult) -> Unit
            val httpResult = HttpResult.Success("Success", "OK",200, 100)
            callback.invoke(httpResult)
        }

        // Act
        var result: NetworkResult? = null
        apiUseCase.execute(method, url, headers, parameters, body, files) { res -> result = res }

        // Assert
        assert(result is NetworkResult.Success)
        assert((result as NetworkResult.Success).response == "Success")
    }


    @Test
    fun `test API request with error response`() {
        // Arrange
        val method = "GET"
        val url = "https://example.com/api"
        val headers = mapOf("Authorization" to "Bearer token")
        val parameters = emptyMap<String, String>()
        val body: String? = null
        val files: Map<String, File?>? = null

        every { requestRepository.sendRequest(method, url, headers, parameters, body, files, any()) } answers {
            val callback = it.invocation.args[6] as (HttpResult) -> Unit
            val httpResult = HttpResult.Error("Not Found", 404)
            callback.invoke(httpResult)
        }

        // Act
        var result: NetworkResult? = null
        apiUseCase.execute(method, url, headers, parameters, body, files) { res -> result = res }

        // Assert
        assert(result is NetworkResult.Error)
        assert((result as NetworkResult.Error).message == "Not Found")
        assert((result as NetworkResult.Error).responseCode == 404)
    }

    @Test
    fun `test API request with exception`() {
        // Arrange
        val method = "GET"
        val url = "https://example.com/api"
        val headers = mapOf("Authorization" to "Bearer token")
        val parameters = emptyMap<String, String>()
        val body: String? = null
        val files: Map<String, File?>? = null

        every { requestRepository.sendRequest(method, url, headers, parameters, body, files, any()) } answers {
            val callback = it.invocation.args[6] as (HttpResult) -> Unit
            val httpResult = HttpResult.Exception(Exception("Network Error"))
            callback.invoke(httpResult)
        }

        // Act
        var result: NetworkResult? = null
        apiUseCase.execute(method, url, headers, parameters, body, files) { res -> result = res }

        // Assert
        assert(result is NetworkResult.Error)
        assert((result as NetworkResult.Error).message == "Network Error")
        assert((result as NetworkResult.Error).responseCode == -1)
    }


    @Test
    fun `test API request with null body and files`() {
        // Arrange
        val method = "POST"
        val url = "https://example.com/api"
        val headers = mapOf("Content-Type" to "application/json")
        val parameters = emptyMap<String, String>()
        val body: String? = null
        val files: Map<String, File?>? = null

        every { requestRepository.sendRequest(method, url, headers, parameters, body, files, any()) } answers {
            val callback = it.invocation.args[6] as (HttpResult) -> Unit
            val httpResult = HttpResult.Success("File uploaded", "OK",200, 150)
            callback.invoke(httpResult)
        }

        // Act
        var result: NetworkResult? = null
        apiUseCase.execute(method, url, headers, parameters, body, files) { res -> result = res }

        // Assert
        assert(result is NetworkResult.Success)
        assert((result as NetworkResult.Success).response == "File uploaded")
    }
}