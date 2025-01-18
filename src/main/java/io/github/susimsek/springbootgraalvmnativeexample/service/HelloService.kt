package io.github.susimsek.springbootgraalvmnativeexample.service

import io.github.susimsek.springbootgraalvmnativeexample.dto.GreetingDTO
import org.springframework.stereotype.Service

/**
 * Service for generating greeting messages.
 */
@Service
class HelloService {

    /**
     * Generates a greeting message.
     *
     * @return a `GreetingDTO` containing the message.
     */
    suspend fun getGreeting(): GreetingDTO {
        return GreetingDTO(message = "Hello, GraalVM Native Image!")
    }
}
