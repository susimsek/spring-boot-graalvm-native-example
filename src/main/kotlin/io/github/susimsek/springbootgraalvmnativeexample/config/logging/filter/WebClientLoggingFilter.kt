package io.github.susimsek.springbootgraalvmnativeexample.config.logging.filter

import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogLevel
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogType
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.Source
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter.LogFormatter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.model.HttpLog
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.utils.Obfuscator.obfuscateHeaders
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.wrapper.BufferingClientHttpRequest
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.util.StopWatch
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

class WebClientLoggingFilter(
    private val logFormatter: LogFormatter,
    private val httpLogLevel: HttpLogLevel = HttpLogLevel.FULL
) : ExchangeFilterFunction {

    private val logger = LoggerFactory.getLogger(WebClientLoggingFilter::class.java)
    private val shouldNotLogPatterns: MutableList<Pair<HttpMethod?, String>> = mutableListOf()

    private val sensitiveHeaders: MutableList<String> =
        mutableListOf("Authorization", "Cookie", "Set-Cookie")

    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        if (httpLogLevel == HttpLogLevel.NONE || shouldNotLog(request)) {
            return next.exchange(request)
        }
        return mono { processRequest(request, next) }
    }

    private suspend fun processRequest(request: ClientRequest, next: ExchangeFunction): ClientResponse {
        val stopWatch = StopWatch()
        var requestBody: ByteArray? = null
        stopWatch.start()

        val processedRequest = if (httpLogLevel == HttpLogLevel.FULL) {
            ClientRequest.from(request)
                .body { outputMessage, context ->
                    BufferingClientHttpRequest(outputMessage).let { bufferingRequest ->
                        request.body().insert(bufferingRequest, context)
                            .doOnSuccess { requestBody = bufferingRequest.requestBody }
                    }
                }
                .build()
        } else {
            request
        }

        val response = next.exchange(processedRequest).awaitSingle()
        stopWatch.stop()

        val durationMs = stopWatch.totalTimeMillis
        logRequest(request, requestBody?.toString(StandardCharsets.UTF_8) ?: "")

        return processResponse(response, request, durationMs)
    }

    private suspend fun processResponse(
        response: ClientResponse,
        request: ClientRequest,
        durationMs: Long
    ): ClientResponse {
        var responseBody: ByteArray? = null

        val mutatedResponse = if (httpLogLevel == HttpLogLevel.FULL) {
            val responseHeaders = response.headers().asHttpHeaders()
            if (responseHeaders.contentLength > 0 || responseHeaders.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
                val body = response.bodyToMono(ByteArray::class.java).awaitSingleOrNull()
                responseBody = body
                response.mutate().body(
                    Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(body ?: ByteArray(0)))
                ).build()
            } else {
                response
            }
        } else {
            response
        }

        logResponse(mutatedResponse, request, responseBody?.toString(StandardCharsets.UTF_8) ?: "", durationMs)
        return mutatedResponse
    }

    private fun logRequest(request: ClientRequest, body: String) {
        val logBuilder = HttpLog(
            type = HttpLogType.REQUEST,
            method = request.method(),
            uri = request.url(),
            statusCode = null,
            headers = if (isHttpLogLevel(HttpLogLevel.HEADERS)) {
                obfuscateHeaders(
                    request.headers(),
                    sensitiveHeaders
                )
            } else {
                null
            },
            body = body,
            source = Source.CLIENT,
            durationMs = null
        )
        logger.info("HTTP Request: {}", logFormatter.format(logBuilder))
    }

    private fun logResponse(response: ClientResponse, request: ClientRequest, body: String, durationMs: Long) {
        val logBuilder = HttpLog(
            type = HttpLogType.RESPONSE,
            method = request.method(),
            uri = request.url(),
            statusCode = response.statusCode().value(),
            headers = if (isHttpLogLevel(
                    HttpLogLevel.HEADERS
                )
            ) {
                obfuscateHeaders(
                    response.headers().asHttpHeaders(),
                    sensitiveHeaders
                )
            } else {
                null
            },
            body = body,
            source = Source.CLIENT,
            durationMs = durationMs
        )
        logger.info("HTTP Response: {}", logFormatter.format(logBuilder))
    }

    private fun shouldNotLog(request: ClientRequest): Boolean {
        val requestPath = request.url().path
        val requestMethod = request.method()
        return shouldNotLogPatterns.any { (method, pattern) ->
            (method == null || method == requestMethod) && pathMatches(requestPath, pattern)
        }
    }

    private fun pathMatches(requestPath: String, pattern: String): Boolean {
        val regexPattern = pattern.replace("**", ".*").replace("*", "[^/]*").toRegex()
        return regexPattern.matches(requestPath)
    }

    fun shouldNotLog(method: HttpMethod?, vararg patterns: String): WebClientLoggingFilter {
        patterns.forEach { pattern ->
            shouldNotLogPatterns.add(Pair(method, pattern))
        }
        return this
    }

    fun shouldNotLog(vararg patterns: String): WebClientLoggingFilter {
        return shouldNotLog(null, *patterns)
    }

    fun shouldNotLog(method: HttpMethod): WebClientLoggingFilter {
        return shouldNotLog(method, "/**")
    }

    private fun isHttpLogLevel(level: HttpLogLevel): Boolean {
        return httpLogLevel.ordinal >= level.ordinal
    }

    fun sensitiveHeader(vararg headers: String): WebClientLoggingFilter {
        sensitiveHeaders.addAll(headers)
        return this
    }
}
