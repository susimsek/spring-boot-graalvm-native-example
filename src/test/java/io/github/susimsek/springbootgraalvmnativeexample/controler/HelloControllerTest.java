package io.github.susimsek.springbootgraalvmnativeexample.controler;

import static org.springframework.http.MediaType.TEXT_PLAIN;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = HelloController.class)
class HelloControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void sayHello_shouldReturnGreetingMessage() {
        // Perform GET request to /api/v1/hello
        webTestClient.get()
            .uri("/api/v1/hello")
            .accept(TEXT_PLAIN)
            .exchange() // Perform the exchange
            .expectStatus().isOk() // Verify status is 200 OK
            .expectHeader().contentType("text/plain;charset=UTF-8") // Verify Content-Type is text/plain
            .expectBody(String.class) // Verify response body
            .isEqualTo("Hello, GraalVM Native Image!"); // Assert the exact response
    }
}
