package io.github.susimsek.springbootgraalvmnativeexample.service

import io.github.susimsek.springbootgraalvmnativeexample.dto.GreetingDTO
import io.github.susimsek.springbootgraalvmnativeexample.mapper.HelloMapper
import org.springframework.stereotype.Service

/**
 * Service for generating greeting messages.
 */
@Service
class HelloService(
  private val helloMapper: HelloMapper
) {

  /**
   * Generates a greeting message.
   *
   * @return a `GreetingDTO` containing the message.
   */
  suspend fun getGreeting(): GreetingDTO {
    val message = "Hello, GraalVM Native Image!"
    return helloMapper.toGreetingDTO(message)
  }
}
