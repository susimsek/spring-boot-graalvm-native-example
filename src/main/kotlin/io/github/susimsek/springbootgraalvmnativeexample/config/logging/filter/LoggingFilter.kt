package io.github.susimsek.springbootgraalvmnativeexample.config.logging.filter

import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogLevel
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogType
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.Source
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter.LogFormatter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.model.HttpLog
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.utils.DataBufferCopyUtils
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.util.StopWatch
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

class LoggingFilter(
    private val logFormatter: LogFormatter,
    private val httpLogLevel: HttpLogLevel = HttpLogLevel.FULL
) : WebFilter {

    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)
    private val shouldNotLogPatterns: MutableList<Pair<HttpMethod?, String>> = mutableListOf()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (httpLogLevel == HttpLogLevel.NONE || shouldNotLog(exchange.request)) {
            return chain.filter(exchange)
        }
        return mono { processRequest(exchange, chain) }
    }

    private suspend fun processRequest(exchange: ServerWebExchange, chain: WebFilterChain): Void {
        val stopWatch = StopWatch()
        stopWatch.start()

        var capturedRequestBody = ""
        val decoratedRequest = object : ServerHttpRequestDecorator(exchange.request) {
            override fun getBody(): Flux<DataBuffer> {
                return if (httpLogLevel == HttpLogLevel.FULL) {
                    Flux.from(
                        DataBufferCopyUtils.wrapAndBuffer(super.getBody()) { bytes ->
                            capturedRequestBody = String(bytes, StandardCharsets.UTF_8)
                            logRequest(this, capturedRequestBody)
                        }
                    )
                } else {
                    logRequest(this, "")
                    super.getBody()
                }
            }
        }

        val originalResponse = exchange.response
        val decoratedResponse = object : ServerHttpResponseDecorator(originalResponse) {
            override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
                return if (httpLogLevel == HttpLogLevel.FULL) {
                    val wrappedBody = DataBufferCopyUtils.wrapAndBuffer(body) { bytes ->
                        val responseBody = String(bytes, StandardCharsets.UTF_8)
                        stopWatch.stop()
                        val durationMs = stopWatch.totalTimeMillis
                        logResponse(exchange, responseBody, durationMs)
                    }
                    super.writeWith(wrappedBody)
                } else {
                    stopWatch.stop()
                    val durationMs = stopWatch.totalTimeMillis
                    // Log boş body
                    logResponse(exchange, "", durationMs)
                    super.writeWith(body)
                }
            }

            override fun writeAndFlushWith(body: Publisher<out Publisher<out DataBuffer>>): Mono<Void> {
                return writeWith(Flux.from(body).flatMap { it })
            }
        }

        val mutatedExchange = exchange.mutate()
            .request(decoratedRequest)
            .response(decoratedResponse)
            .build()

        return chain.filter(mutatedExchange).awaitSingle()
    }

    private fun logRequest(request: ServerHttpRequest, body: String) {
        val httpLog = HttpLog(
            type = HttpLogType.REQUEST,
            method = request.method,
            uri = request.uri,
            statusCode = null,
            headers = if (isHttpLogLevel(HttpLogLevel.HEADERS)) request.headers else null,
            body = body,
            source = Source.SERVER,
            durationMs = null
        )
        logger.info("HTTP Request: {}", logFormatter.format(httpLog))
    }

    private fun logResponse(exchange: ServerWebExchange, body: String, durationMs: Long) {
        val request = exchange.request
        val response = exchange.response
        val httpLog = HttpLog(
            type = HttpLogType.RESPONSE,
            method = request.method,
            uri = request.uri,
            statusCode = response.statusCode?.value(),
            headers = if (isHttpLogLevel(HttpLogLevel.HEADERS)) response.headers else null,
            body = body,
            source = Source.SERVER,
            durationMs = durationMs
        )
        logger.info("HTTP Response: {}", logFormatter.format(httpLog))
    }

    private fun shouldNotLog(request: ServerHttpRequest): Boolean {
        val requestPath = request.uri.path
        val requestMethod = request.method
        return shouldNotLogPatterns.any { (method, pattern) ->
            (method == null || method == requestMethod) && pathMatches(requestPath, pattern)
        }
    }

    private fun pathMatches(requestPath: String, pattern: String): Boolean {
        val regexPattern = pattern.replace("**", ".*").replace("*", "[^/]*").toRegex()
        return regexPattern.matches(requestPath)
    }

    fun shouldNotLog(method: HttpMethod?, vararg patterns: String): LoggingFilter {
        patterns.forEach { pattern ->
            shouldNotLogPatterns.add(Pair(method, pattern))
        }
        return this
    }

    fun shouldNotLog(vararg patterns: String): LoggingFilter {
        return shouldNotLog(null, *patterns)
    }

    fun shouldNotLog(method: HttpMethod): LoggingFilter {
        return shouldNotLog(method, "/**")
    }

    private fun isHttpLogLevel(level: HttpLogLevel): Boolean {
        return httpLogLevel.ordinal >= level.ordinal
    }
}
