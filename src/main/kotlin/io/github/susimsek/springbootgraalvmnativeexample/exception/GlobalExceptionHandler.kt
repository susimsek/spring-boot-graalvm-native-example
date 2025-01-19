package io.github.susimsek.springbootgraalvmnativeexample.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerWebExchange

/**
 * Global exception handler for the application.
 *
 * This class provides centralized exception handling across all @RequestMapping methods
 * through @ExceptionHandler methods. It extends [CoResponseEntityExceptionHandler] to
 * inherit the base implementation and override specific behaviors.
 */
@ControllerAdvice
class GlobalExceptionHandler : CoResponseEntityExceptionHandler() {

  /**
   * Handles generic exceptions.
   *
   * This method is invoked whenever an unhandled exception occurs. It returns a response
   * with a generic error message and a `500 Internal Server Error` status code.
   *
   * @param ex the exception to handle.
   * @param exchange the current server web exchange.
   * @return a [ResponseEntity] containing problem details and HTTP status.
   */
  @ExceptionHandler(Exception::class)
  suspend fun handleGenericException(
    ex: Exception,
    exchange: ServerWebExchange
  ): ResponseEntity<Any> {
    // Create a ProblemDetail object for the exception.
    val problemDetail = createProblemDetail(
      status = HttpStatus.INTERNAL_SERVER_ERROR,
      detail = "An internal server error occurred. Please try again later.",
      errorCode = "server_error"
    )
    // Return a ResponseEntity with the problem detail and HTTP status.
    return ResponseEntity(problemDetail as Any, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR)
  }
}
