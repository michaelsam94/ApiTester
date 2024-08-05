package com.michael.apitester.usecases

import com.michael.apitester.di.TestServiceLocator
import com.michael.data.repository.RequestRepository
import com.michael.domain.SortOption
import com.michael.domain.usecases.SortResponsesUseCase
import com.michael.data.dto.HttpResponseDto
import com.michael.data.remote.ApiService
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SortResponsesUseCaseTest {

    private lateinit var requestRepository: RequestRepository
    private lateinit var sortResponsesUseCase: SortResponsesUseCase
    private lateinit var apiService: ApiService


    @Before
    fun setup() {
        requestRepository = mockk()
        apiService = mockk()// Create a mock for the RequestRepository

        // Initialize the test service locator
        TestServiceLocator.initTest(apiService,requestRepository)
        sortResponsesUseCase = TestServiceLocator.getSortResponsesUseCase()
    }

    @Test
    fun `test sort responses by ascending order`() {
        // Arrange
        val responses = listOf(
            createHttpResponseDto(requestTime = 3000),
            createHttpResponseDto(requestTime = 1000),
            createHttpResponseDto(requestTime = 2000)
        )

        every { requestRepository.getResponses(any()) } answers {
            it.invocation.args[0].let { callback ->
                (callback as (List<HttpResponseDto>) -> Unit).invoke(responses)
            }
        }

        // Act
        sortResponsesUseCase.execute(SortOption.Ascending) { sortedResponses ->
            // Assert
            assertEquals(1000, sortedResponses[0].requestTime)
            assertEquals(2000, sortedResponses[1].requestTime)
            assertEquals(3000, sortedResponses[2].requestTime)
        }
    }

    @Test
    fun `test sort responses by descending order`() {
        // Arrange
        val responses = listOf(
            createHttpResponseDto(requestTime = 1000),
            createHttpResponseDto(requestTime = 3000),
            createHttpResponseDto(requestTime = 2000)
        )

        every { requestRepository.getResponses(any()) } answers {
            it.invocation.args[0].let { callback ->
                (callback as (List<HttpResponseDto>) -> Unit).invoke(responses)
            }
        }

        // Act
        sortResponsesUseCase.execute(SortOption.Descending) { sortedResponses ->
            // Assert
            assertEquals(3000, sortedResponses[0].requestTime)
            assertEquals(2000, sortedResponses[1].requestTime)
            assertEquals(1000, sortedResponses[2].requestTime)
        }
    }

    @Test
    fun `test sort responses with no sorting`() {
        // Arrange
        val responses = listOf(
            createHttpResponseDto(requestTime = 2000),
            createHttpResponseDto(requestTime = 1000),
            createHttpResponseDto(requestTime = 3000)
        )

        every { requestRepository.getResponses(any()) } answers {
            it.invocation.args[0].let { callback ->
                (callback as (List<HttpResponseDto>) -> Unit).invoke(responses)
            }
        }

        // Act
        sortResponsesUseCase.execute(SortOption.NoSorting) { sortedResponses ->
            // Assert
            assertEquals(2000, sortedResponses[0].requestTime)
            assertEquals(1000, sortedResponses[1].requestTime)
            assertEquals(3000, sortedResponses[2].requestTime)
        }
    }

    @Test
    fun `test sort responses with null sort option`() {
        // Arrange
        val responses = listOf(
            createHttpResponseDto(requestTime = 2000),
            createHttpResponseDto(requestTime = 3000),
            createHttpResponseDto(requestTime = 1000)
        )

        every { requestRepository.getResponses(any()) } answers {
            it.invocation.args[0].let { callback ->
                (callback as (List<HttpResponseDto>) -> Unit).invoke(responses)
            }
        }

        // Act
        sortResponsesUseCase.execute(null) { sortedResponses ->
            // Assert
            assertEquals(2000, sortedResponses[0].requestTime)
            assertEquals(3000, sortedResponses[1].requestTime)
            assertEquals(1000, sortedResponses[2].requestTime)
        }
    }

    @Test
    fun `test sort responses with empty response list`() {
        // Arrange
        val responses = emptyList<HttpResponseDto>()

        every { requestRepository.getResponses(any()) } answers {
            it.invocation.args[0].let { callback ->
                (callback as (List<HttpResponseDto>) -> Unit).invoke(responses)
            }
        }

        // Act
        sortResponsesUseCase.execute(SortOption.Ascending) { sortedResponses ->
            // Assert
            assertEquals(0, sortedResponses.size)
        }
    }

    private fun createHttpResponseDto(
        requestUrl: String = "https://example.com",
        responseCode: Int = 200,
        error: String? = null,
        headers: Map<String, String> = emptyMap(),
        body: String = "",
        params: Map<String, String> = emptyMap(),
        requestTime: Int,
        requestSchema: String = "https",
        requestMethod: String = "GET"
    ): HttpResponseDto {
        return HttpResponseDto(
            requestUrl = requestUrl,
            responseCode = responseCode,
            error = error,
            headers = headers,
            body = body,
            params = params,
            requestTime = requestTime,
            requestSchema = requestSchema,
            requestMethod = requestMethod
        )
    }
}
