package com.example.apitester.usecases

import com.example.apitester.di.ServiceLocator
import com.example.apitester.di.TestServiceLocator
import com.example.data.dto.HttpResponseDto
import com.example.data.remote.ApiService
import com.example.data.repository.RequestRepository
import com.example.domain.FilterOption
import com.example.domain.model.HttpResponse
import com.example.domain.usecases.ApiUseCase
import com.example.domain.usecases.FilterResponsesUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class FilterResponsesUseCaseTest {

    private lateinit var requestRepository: RequestRepository
    private lateinit var filterResponsesUseCase: FilterResponsesUseCase
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        requestRepository = mockk()
        apiService = mockk() // Create a mock for the RequestRepository

        // Initialize the test service locator
        TestServiceLocator.initTest(apiService, requestRepository)
        filterResponsesUseCase = TestServiceLocator.getFilterResponsesUseCase()
    }

    @Test
    fun `test filter by GET request type`() {
        // Arrange
        val mockResponses = listOf(
            HttpResponse(
                requestUrl = "https://example.com/api1",
                responseCode = 200,
                error = null,
                headers = emptyMap(),
                body = "",
                params = emptyMap(),
                requestTime = 1000,
                requestSchema = "https",
                requestMethod = "GET"
            ),
            HttpResponse(
                requestUrl = "https://example.com/api2",
                responseCode = 200,
                error = null,
                headers = emptyMap(),
                body = "",
                params = emptyMap(),
                requestTime = 1000,
                requestSchema = "https",
                requestMethod = "POST"
            )
        )

        every { requestRepository.getResponses(any()) } answers {
            val callback = it.invocation.args[0] as (List<HttpResponseDto>) -> Unit
            callback(mockResponses.map {
                HttpResponseDto(
                    requestUrl = it.requestUrl,
                    responseCode = it.responseCode,
                    error = it.error,
                    headers = it.headers,
                    body = it.body,
                    params = it.params,
                    requestTime = it.requestTime,
                    requestMethod = it.requestMethod,
                    requestSchema = it.requestSchema,
                )
            })
        }

        // Act
        var filteredResponses: List<HttpResponse>? = null
        filterResponsesUseCase.execute(FilterOption.Get, null) { responses ->
            filteredResponses = responses
        }

        // Assert
        assert(filteredResponses?.size == 1)
        assert(filteredResponses?.first()?.requestMethod == "GET")
    }

    @Test
    fun `test filter by POST request type`() {
        // Arrange
        val mockResponses = listOf(
            HttpResponse(
                requestUrl = "https://example.com/api1",
                responseCode = 200,
                error = null,
                headers = emptyMap(),
                body = "",
                params = emptyMap(),
                requestTime = 1000,
                requestSchema = "https",
                requestMethod = "POST"
            ),
            HttpResponse(
                requestUrl = "https://example.com/api2",
                responseCode = 200,
                error = null,
                headers = emptyMap(),
                body = "",
                params = emptyMap(),
                requestTime = 1000,
                requestSchema = "https",
                requestMethod = "GET"
            )
        )

        every { requestRepository.getResponses(any()) } answers {
            val callback = it.invocation.args[0] as (List<HttpResponseDto>) -> Unit
            callback(mockResponses.map {
                HttpResponseDto(
                    requestUrl = it.requestUrl,
                    responseCode = it.responseCode,
                    error = it.error,
                    headers = it.headers,
                    body = it.body,
                    params = it.params,
                    requestTime = it.requestTime,
                    requestMethod = it.requestMethod,
                    requestSchema = it.requestSchema,
                )
            })
        }

        // Act
        var filteredResponses: List<HttpResponse>? = null
        filterResponsesUseCase.execute(FilterOption.Post, null) { responses ->
            filteredResponses = responses
        }

        // Assert
        assert(filteredResponses?.size == 1)
        assert(filteredResponses?.first()?.requestMethod == "POST")
    }

    @Test
    fun `test filter by successful response status`() {
        // Arrange
        val mockResponses = listOf(
            HttpResponse(
                requestUrl = "https://example.com/api1",
                responseCode = 200,
                error = null,
                headers = emptyMap(),
                body = "",
                params = emptyMap(),
                requestTime = 1000,
                requestSchema = "https",
                requestMethod = "GET"
            ),
            HttpResponse(
                requestUrl = "https://example.com/api2",
                responseCode = 404,
                error = "Not Found",
                headers = emptyMap(),
                body = "",
                params = emptyMap(),
                requestTime = 1000,
                requestSchema = "https",
                requestMethod = "GET"
            )
        )

        every { requestRepository.getResponses(any()) } answers {
            val callback = it.invocation.args[0] as (List<HttpResponseDto>) -> Unit
            callback(mockResponses.map {
                HttpResponseDto(
                    requestUrl = it.requestUrl,
                    responseCode = it.responseCode,
                    error = it.error,
                    headers = it.headers,
                    body = it.body,
                    params = it.params,
                    requestTime = it.requestTime,
                    requestMethod = it.requestMethod,
                    requestSchema = it.requestSchema,
                )
            })
        }

        // Act
        var filteredResponses: List<HttpResponse>? = null
        filterResponsesUseCase.execute(
            null,
            FilterOption.Success
        ) { responses -> filteredResponses = responses }

        // Assert
        assert(filteredResponses?.size == 1)
        assert(filteredResponses?.first()?.responseCode == 200)
    }

    @Test
    fun `test filter by failure response status`() {
        // Arrange
        val mockResponses = listOf(
            HttpResponse(
                requestUrl = "https://example.com/api1",
                responseCode = 200,
                error = null,
                headers = emptyMap(),
                body = "",
                params = emptyMap(),
                requestTime = 1000,
                requestSchema = "https",
                requestMethod = "GET"
            ),
            HttpResponse(
                requestUrl = "https://example.com/api2",
                responseCode = 404,
                error = "Not Found",
                headers = emptyMap(),
                body = "",
                params = emptyMap(),
                requestTime = 1000,
                requestSchema = "https",
                requestMethod = "GET"
            )
        )

        every { requestRepository.getResponses(any()) } answers {
            val callback = it.invocation.args[0] as (List<HttpResponseDto>) -> Unit
            callback(mockResponses.map {
                HttpResponseDto(
                    requestUrl = it.requestUrl,
                    responseCode = it.responseCode,
                    error = it.error,
                    headers = it.headers,
                    body = it.body,
                    params = it.params,
                    requestTime = it.requestTime,
                    requestMethod = it.requestMethod,
                    requestSchema = it.requestSchema,
                )
            })
        }

        // Act
        var filteredResponses: List<HttpResponse>? = null
        filterResponsesUseCase.execute(
            null,
            FilterOption.Failure
        ) { responses -> filteredResponses = responses }

        // Assert
        assert(filteredResponses?.size == 1)
        assert(filteredResponses?.first()?.responseCode == 404)
    }

    @Test
    fun `test no filters applied`() {
        // Arrange
        val mockResponses = listOf(
            HttpResponse(
                requestUrl = "https://example.com/api1",
                responseCode = 200,
                error = null,
                headers = emptyMap(),
                body = "",
                params = emptyMap(),
                requestTime = 1000,
                requestSchema = "https",
                requestMethod = "GET"
            ),
            HttpResponse(
                requestUrl = "https://example.com/api2",
                responseCode = 404,
                error = "Not Found",
                headers = emptyMap(),
                body = "",
                params = emptyMap(),
                requestTime = 1000,
                requestSchema = "https",
                requestMethod = "POST"
            )
        )

        every { requestRepository.getResponses(any()) } answers {
            val callback = it.invocation.args[0] as (List<HttpResponseDto>) -> Unit
            callback(mockResponses.map {
                HttpResponseDto(
                    requestUrl = it.requestUrl,
                    responseCode = it.responseCode,
                    error = it.error,
                    headers = it.headers,
                    body = it.body,
                    params = it.params,
                    requestTime = it.requestTime,
                    requestMethod = it.requestMethod,
                    requestSchema = it.requestSchema,
                )
            })
        }

        // Act
        var filteredResponses: List<HttpResponse>? = null
        filterResponsesUseCase.execute(null, null) { responses -> filteredResponses = responses }

        // Assert
        assert(filteredResponses?.size == 2) // Expecting both responses
    }
}