package io.github.susimsek.springbootgraalvmnativeexample.exception

import io.github.susimsek.springbootgraalvmnativeexample.dto.Violation
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.NotAcceptableStatusException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerErrorException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.UnsupportedMediaTypeStatusException

/**
 * Base class for handling global exceptions in a Spring WebFlux application.
 * Provides default implementations for handling various HTTP exceptions.
 * Can be extended to provide custom behavior for specific exception types.
 */
abstract class CoResponseEntityExceptionHandler : MessageSourceAware {

    private var messageSource: MessageSource? = null

    /**
     * Sets the [MessageSource] to allow for localization of error messages.
     *
     * @param messageSource the [MessageSource] to be used.
     */
    override fun setMessageSource(messageSource: MessageSource) {
        this.messageSource = messageSource
    }

    /**
     * Retrieves the configured [MessageSource].
     *
     * @return the configured [MessageSource], or `null` if not set.
     */
    protected fun getMessageSource(): MessageSource? = this.messageSource

    /**
     * Handles various exception types and maps them to appropriate HTTP responses.
     *
     * @param ex The exception to be handled.
     * @param exchange The current [ServerWebExchange].
     * @return A [ResponseEntity] containing the error details.
     */
    @ExceptionHandler(
        MethodNotAllowedException::class,
        NotAcceptableStatusException::class,
        UnsupportedMediaTypeStatusException::class,
        WebExchangeBindException::class,
        ServerWebInputException::class,
        ServerErrorException::class,
        ResponseStatusException::class
    )
    suspend fun handleException(ex: Exception, exchange: ServerWebExchange): ResponseEntity<Any> {
        return when (ex) {
            is MethodNotAllowedException -> handleMethodNotAllowedException(
                ex,
                ex.headers,
                ex.statusCode,
                exchange
            )
            is NotAcceptableStatusException -> handleNotAcceptableStatusException(
                ex,
                ex.headers,
                ex.statusCode,
                exchange
            )
            is UnsupportedMediaTypeStatusException -> handleUnsupportedMediaTypeStatusException(
                ex,
                ex.headers,
                ex.statusCode,
                exchange
            )
            is WebExchangeBindException -> handleWebExchangeBindException(
                ex,
                ex.headers,
                ex.statusCode,
                exchange
            )
            is ServerWebInputException -> handleServerWebInputException(
                ex,
                ex.headers,
                ex.statusCode,
                exchange
            )
            is ServerErrorException -> handleServerErrorException(
                ex,
                ex.headers,
                ex.statusCode,
                exchange
            )
            is ResponseStatusException -> handleResponseStatusException(
                ex,
                ex.headers,
                ex.statusCode,
                exchange
            )
            else -> createDefaultErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, exchange)
        }
    }

    /**
     * Handles [ServerErrorException].
     */
    protected open suspend fun handleServerErrorException(
        ex: ServerErrorException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            detail = "A server error occurred while processing the request.",
            errorCode = "server_error"
        )
        return ResponseEntity(problemDetail as Any, headers, status)
    }

    /**
     * Handles [MethodNotAllowedException].
     */
    protected open suspend fun handleMethodNotAllowedException(
        ex: MethodNotAllowedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            status = HttpStatus.METHOD_NOT_ALLOWED,
            detail = "Requested HTTP method is not supported.",
            errorCode = "method_not_allowed"
        ).apply {
            setProperty("allowedMethods", ex.supportedMethods?.joinToString(", ") ?: "None")
        }
        return ResponseEntity(problemDetail as Any, headers, status)
    }

    /**
     * Handles [NotAcceptableStatusException].
     */
    protected open suspend fun handleNotAcceptableStatusException(
        ex: NotAcceptableStatusException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            status = HttpStatus.NOT_ACCEPTABLE,
            detail = "Requested media type is not acceptable.",
            errorCode = "not_acceptable"
        )
        return ResponseEntity(problemDetail as Any, headers, status)
    }

    /**
     * Handles [UnsupportedMediaTypeStatusException].
     */
    protected open suspend fun handleUnsupportedMediaTypeStatusException(
        ex: UnsupportedMediaTypeStatusException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            detail = "Requested media type is not supported.",
            errorCode = "unsupported_media_type"
        ).apply {
            setProperty("supportedMediaTypes", ex.supportedMediaTypes.joinToString(", ") { it.toString() })
        }
        return ResponseEntity(problemDetail as Any, headers, status)
    }

    /**
     * Handles [WebExchangeBindException].
     */
    protected open suspend fun handleWebExchangeBindException(
        ex: WebExchangeBindException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): ResponseEntity<Any> {
        val violations = ex.fieldErrors.map { Violation(it) } + ex.globalErrors.map { Violation(it) }
        val problemDetail = createProblemDetail(
            status = HttpStatus.BAD_REQUEST,
            detail = "Validation error occurred.",
            errorCode = "invalid_request"
        ).apply {
            setProperty("violations", violations)
        }
        return ResponseEntity(problemDetail as Any, headers, status)
    }

    /**
     * Handles [ServerWebInputException].
     */
    protected open suspend fun handleServerWebInputException(
        ex: ServerWebInputException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            status = HttpStatus.BAD_REQUEST,
            detail = "Invalid input.",
            errorCode = "invalid_request"
        )
        return ResponseEntity(problemDetail as Any, headers, status)
    }

    /**
     * Handles [ResponseStatusException].
     */
    protected open suspend fun handleResponseStatusException(
        ex: ResponseStatusException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            status = ex.statusCode,
            detail = ex.reason ?: "Error occurred.",
            errorCode = "response_status_exception"
        )
        return ResponseEntity(problemDetail as Any, headers, status)
    }

    /**
     * Creates a default error response for uncaught exceptions.
     */
    private fun createDefaultErrorResponse(
        ex: Exception,
        status: HttpStatus,
        exchange: ServerWebExchange
    ): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            status = status,
            detail = "An internal server error occurred. Please try again later.",
            errorCode = "server_error"
        )
        return ResponseEntity(problemDetail as Any, HttpHeaders(), status)
    }

    /**
     * Creates a [ProblemDetail] for the given parameters.
     */
    protected fun createProblemDetail(
        status: HttpStatusCode,
        detail: String,
        errorCode: String
    ): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(status, detail).apply {
            setProperty("error", errorCode)
        }
    }
}
