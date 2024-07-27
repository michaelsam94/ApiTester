package com.example.apitester.usecases

import com.example.apitester.di.TestServiceLocator
import com.example.data.remote.ApiService
import com.example.data.repository.RequestRepository
import com.example.domain.model.HttpResponse
import com.example.domain.usecases.SaveResponseUseCase
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SaveResponseUseCaseTest {

    private lateinit var requestRepository: RequestRepository
    private lateinit var saveResponseUseCase: SaveResponseUseCase
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        requestRepository = mockk(relaxed = true)
        apiService = mockk(relaxed = true)

        // Initialize the test service locator
        TestServiceLocator.initTest(apiService, requestRepository)
        saveResponseUseCase = TestServiceLocator.getSaveResponseUseCase()
    }

    @Test
    fun `test save response with valid data`() {
        // Arrange
        val httpResponse = HttpResponse(
            requestUrl = "https://example.com/api",
            responseCode = 200,
            error = null,
            headers = mapOf("Content-Type" to "application/json"),
            body = "{\"key\":\"value\"}",
            params = emptyMap(),
            requestTime = 1000,
            requestMethod = "GET",
            requestSchema = "https"
        )

        // Act
        saveResponseUseCase.execute(httpResponse)

        // Assert
        verify { requestRepository.saveResponse(any()) }
    }

    @Test
    fun `test save response with error message`() {
        // Arrange
        val httpResponse = HttpResponse(
            requestUrl = "https://example.com/api",
            responseCode = 404,
            error = "Not Found",
            headers = emptyMap(),
            body = "",
            params = emptyMap(),
            requestTime = 2000,
            requestMethod = "GET",
            requestSchema = "https"
        )

        // Act
        saveResponseUseCase.execute(httpResponse)

        // Assert
        verify { requestRepository.saveResponse(any()) }
    }

    @Test
    fun `test save response with empty body`() {
        // Arrange
        val httpResponse = HttpResponse(
            requestUrl = "https://example.com/api",
            responseCode = 200,
            error = null,
            headers = emptyMap(),
            body = "",
            params = emptyMap(),
            requestTime = 3000,
            requestMethod = "POST",
            requestSchema = "https"
        )

        // Act
        saveResponseUseCase.execute(httpResponse)

        // Assert
        verify { requestRepository.saveResponse(any()) }
    }

    @Test
    fun `test save response with different HTTP methods`() {
        // Arrange
        val methods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")
        methods.forEach { method ->
            val httpResponse = HttpResponse(
                requestUrl = "https://example.com/api",
                responseCode = 200,
                error = null,
                headers = emptyMap(),
                body = "{}",
                params = emptyMap(),
                requestTime = 4000,
                requestMethod = method,
                requestSchema = "https"
            )

            // Act
            saveResponseUseCase.execute(httpResponse)

            // Assert
            verify { requestRepository.saveResponse(any()) }
        }
    }

    @Test
    fun `test save response with null headers`() {
        // Arrange
        val httpResponse = HttpResponse(
            requestUrl = "https://example.com/api",
            responseCode = 200,
            error = null,
            headers = emptyMap(),
            body = "{\"key\":\"value\"}",
            params = emptyMap(),
            requestTime = 5000,
            requestMethod = "GET",
            requestSchema = "https"
        )

        // Act
        saveResponseUseCase.execute(httpResponse)

        // Assert
        verify { requestRepository.saveResponse(any()) }
    }
}
