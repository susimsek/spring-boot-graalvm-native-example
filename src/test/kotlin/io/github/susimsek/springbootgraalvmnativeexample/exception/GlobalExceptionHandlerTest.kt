package io.github.susimsek.springbootgraalvmnativeexample.exception

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertEquals

class GlobalExceptionHandlerTest {

    private val globalExceptionHandler = GlobalExceptionHandler()

    @Test
    fun `should handle generic exception and return 500 status with error details`() {
        runBlocking {
            // Arrange
            val exception = Exception("Test exception")
            val mockExchange = Mockito.mock(ServerWebExchange::class.java)

            // Act
            val responseEntity = globalExceptionHandler.handleGenericException(exception, mockExchange)

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.statusCode)

            val problemDetail = responseEntity.body as ProblemDetail
            val error = problemDetail.properties?.get("error")
            assertEquals("server_error", error)
            assertEquals("An internal server error occurred. Please try again later.", problemDetail.detail)
        }
    }
}
