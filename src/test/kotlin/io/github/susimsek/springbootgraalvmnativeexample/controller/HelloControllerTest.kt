package io.github.susimsek.springbootgraalvmnativeexample.controller

import io.github.susimsek.springbootgraalvmnativeexample.dto.GreetingDTO
import io.github.susimsek.springbootgraalvmnativeexample.service.HelloService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(HelloController::class)
class HelloControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoBean
    private lateinit var helloService: HelloService

    @Test
    fun `should return greeting message`(): Unit = runBlocking {
        // Arrange
        val expectedMessage = "Hello, GraalVM Native Image!"
        `when`(helloService.getGreeting()).thenReturn(GreetingDTO(message = expectedMessage))

        // Act
        val responseSpec = webTestClient.get()
            .uri("/api/v1/hello")
            .exchange()

        // Assert
        responseSpec.expectStatus().isOk
            .expectBody()
            .jsonPath("$.message").isEqualTo(expectedMessage)
    }
}
