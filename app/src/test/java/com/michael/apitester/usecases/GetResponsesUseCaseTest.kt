package com.michael.apitester.usecases

import com.michael.apitester.di.TestServiceLocator
import com.michael.data.dto.HttpResponseDto
import com.michael.data.remote.ApiService
import com.michael.data.repository.RequestRepository
import com.michael.domain.model.HttpResponse
import com.michael.domain.usecases.GetResponsesUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class GetResponsesUseCaseTest {

    private lateinit var requestRepository: RequestRepository
    private lateinit var getResponsesUseCase: GetResponsesUseCase
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        requestRepository = mockk()
        apiService = mockk()
        // Create a mock for the RequestRepository

        // Initialize the test service locator
        TestServiceLocator.initTest(apiService,requestRepository)
        getResponsesUseCase = TestServiceLocator.getResponseUseCase()
    }

    @Test
    fun `test get responses returns list of HttpResponse`() {
        // Arrange
        val mockResponses = listOf(
            HttpResponseDto(requestUrl = "https://example.com/api1", responseCode = 200, error = null, headers = emptyMap(), body = "", params = emptyMap(), requestTime = 1000, requestMethod = "GET", requestSchema = "https"),
            HttpResponseDto(requestUrl = "https://example.com/api2", responseCode = 404, error = "Not Found", headers = emptyMap(), body = "", params = emptyMap(), requestTime = 2000, requestMethod = "POST", requestSchema = "https")
        )

        every { requestRepository.getResponses(any()) } answers {
            val callback = it.invocation.args[0] as (List<HttpResponseDto>) -> Unit
            callback(mockResponses)
        }

        // Act
        var responses: List<HttpResponse>? = null
        getResponsesUseCase.execute { result -> responses = result }

        // Assert
        assert(responses?.size == 2)
        assert(responses?.first()?.requestUrl == "https://example.com/api1")
        assert(responses?.last()?.responseCode == 404)
    }

    @Test
    fun `test get responses handles empty list`() {
        // Arrange
        val mockResponses = emptyList<HttpResponseDto>()

        every { requestRepository.getResponses(any()) } answers {
            val callback = it.invocation.args[0] as (List<HttpResponseDto>) -> Unit
            callback(mockResponses)
        }

        // Act
        var responses: List<HttpResponse>? = null
        getResponsesUseCase.execute { result -> responses = result }

        // Assert
        assert(responses?.isEmpty() == true)
    }

    @Test
    fun `test get responses with one successful response`() {
        // Arrange
        val mockResponses = listOf(
            HttpResponseDto(requestUrl = "https://example.com/api1", responseCode = 200, error = null, headers = emptyMap(), body = "", params = emptyMap(), requestTime = 1000, requestMethod = "GET", requestSchema = "https")
        )

        every { requestRepository.getResponses(any()) } answers {
            val callback = it.invocation.args[0] as (List<HttpResponseDto>) -> Unit
            callback(mockResponses)
        }

        // Act
        var responses: List<HttpResponse>? = null
        getResponsesUseCase.execute { result -> responses = result }

        // Assert
        assert(responses?.size == 1)
        assert(responses?.first()?.responseCode == 200)
    }

    @Test
    fun `test get responses with multiple request methods`() {
        // Arrange
        val mockResponses = listOf(
            HttpResponseDto(requestUrl = "https://example.com/api1", responseCode = 200, error = null, headers = emptyMap(), body = "", params = emptyMap(), requestTime = 1000, requestMethod = "GET", requestSchema = "https"),
            HttpResponseDto(requestUrl = "https://example.com/api2", responseCode = 404, error = "Not Found", headers = emptyMap(), body = "", params = emptyMap(), requestTime = 2000, requestMethod = "POST", requestSchema = "https"),
            HttpResponseDto(requestUrl = "https://example.com/api3", responseCode = 500, error = "Internal Server Error", headers = emptyMap(), body = "", params = emptyMap(), requestTime = 3000, requestMethod = "PUT", requestSchema = "https")
        )

        every { requestRepository.getResponses(any()) } answers {
            val callback = it.invocation.args[0] as (List<HttpResponseDto>) -> Unit
            callback(mockResponses)
        }

        // Act
        var responses: List<HttpResponse>? = null
        getResponsesUseCase.execute { result -> responses = result }

        // Assert
        assert(responses?.size == 3)
        assert(responses?.any { it.requestMethod == "PUT" } == true)
        assert(responses?.any { it.requestMethod == "POST" } == true)
    }

    @Test
    fun `test get responses with varied response codes`() {
        // Arrange
        val mockResponses = listOf(
            HttpResponseDto(requestUrl = "https://example.com/api1", responseCode = 200, error = null, headers = emptyMap(), body = "", params = emptyMap(), requestTime = 1000, requestMethod = "GET", requestSchema = "https"),
            HttpResponseDto(requestUrl = "https://example.com/api2", responseCode = 404, error = "Not Found", headers = emptyMap(), body = "", params = emptyMap(), requestTime = 2000, requestMethod = "POST", requestSchema = "https"),
            HttpResponseDto(requestUrl = "https://example.com/api3", responseCode = 500, error = "Internal Server Error", headers = emptyMap(), body = "", params = emptyMap(), requestTime = 3000, requestMethod = "PUT", requestSchema = "https")
        )

        every { requestRepository.getResponses(any()) } answers {
            val callback = it.invocation.args[0] as (List<HttpResponseDto>) -> Unit
            callback(mockResponses)
        }

        // Act
        var responses: List<HttpResponse>? = null
        getResponsesUseCase.execute { result -> responses = result }

        // Assert
        assert(responses?.size == 3)
        assert(responses?.any { it.responseCode == 200 } == true)
        assert(responses?.any { it.responseCode == 404 } == true)
        assert(responses?.any { it.responseCode == 500 } == true)
    }
}