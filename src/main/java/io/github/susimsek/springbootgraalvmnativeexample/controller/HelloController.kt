package io.github.susimsek.springbootgraalvmnativeexample.controller

import io.github.susimsek.springbootgraalvmnativeexample.dto.GreetingDTO
import io.github.susimsek.springbootgraalvmnativeexample.service.HelloService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller for greeting messages.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "hello", description = "Endpoints for Hello World operations")
class HelloController(
  private val helloService: HelloService
) {

  /**
   * `GET /hello` : Returns a greeting message wrapped in a DTO.
   *
   * @return the greeting message as a `GreetingDTO`.
   */
  @Operation(
    summary = "Say Hello",
    description = "Returns a greeting message wrapped in a DTO."
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successful operation",
    content = [
      Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = Schema(implementation = GreetingDTO::class)
      )
    ]
  )
  @GetMapping("/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
  suspend fun sayHello(): GreetingDTO {
    return helloService.getGreeting()
  }
}
