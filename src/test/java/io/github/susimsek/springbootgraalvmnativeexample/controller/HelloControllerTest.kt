package io.github.susimsek.springbootgraalvmnativeexample.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(HelloController::class)
class HelloControllerTest {

  @Autowired
  private lateinit var webTestClient: WebTestClient

  @Test
  fun `should return greeting message`() {
    webTestClient.get()
      .uri("/api/v1/hello")
      .exchange()
      .expectStatus().isOk
      .expectBody(String::class.java)
      .isEqualTo("Hello, GraalVM Native Image!")
  }
}
