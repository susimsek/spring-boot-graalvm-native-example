package io.github.susimsek.springbootgraalvmnativeexample.config.logging.filter

import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogLevel
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogType
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.Source
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter.LogFormatter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.model.HttpLog
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.utils.DataBufferCopyUtils
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.utils.Obfuscator
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.util.AntPathMatcher
import org.springframework.util.StopWatch
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.nio.charset.StandardCharsets

class LoggingFilter private constructor(
    private val logFormatter: LogFormatter,
    private val obfuscator: Obfuscator,
    private val httpLogLevel: HttpLogLevel,
    private val sensitiveHeaders: List<String>,
    private val shouldNotLogPatterns: List<Pair<HttpMethod?, String>>,
    private val sensitiveJsonBodyFields: List<String>,
    private val sensitiveParameters: List<String>
) : WebFilter {

    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)
    private val pathMatcher = AntPathMatcher()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        if (httpLogLevel == HttpLogLevel.NONE || shouldNotLog(request)) {
            return chain.filter(exchange)
        }
        return mono { processRequest(exchange, chain) }
    }

    private suspend fun processRequest(exchange: ServerWebExchange, chain: WebFilterChain): Void {
        val stopWatch = StopWatch()
        stopWatch.start()
        val uri = maskUri(exchange.request)

        val request = exchange.request
        val decoratedRequest = object : ServerHttpRequestDecorator(exchange.request) {
            override fun getBody(): Flux<DataBuffer> {
                return if (httpLogLevel == HttpLogLevel.FULL) {
                    Flux.from(
                        DataBufferCopyUtils.wrapAndBuffer(super.getBody()) { bytes ->
                            val capturedRequestBody = String(bytes, StandardCharsets.UTF_8)
                            logRequest(
                                this,
                                uri,
                                capturedRequestBody
                            )
                        }
                    )
                } else {
                    logRequest(this, uri, "")
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
                        logResponse(
                            exchange.response,
                            uri,
                            exchange.request.method,
                            responseBody,
                            durationMs
                        )
                    }
                    super.writeWith(wrappedBody)
                } else {
                    stopWatch.stop()
                    val durationMs = stopWatch.totalTimeMillis
                    logResponse(
                        exchange.response,
                        uri,
                        request.method,
                        "",
                        durationMs
                    )
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

    private fun logRequest(
        request: ServerHttpRequest,
        uri: URI,
        body: String
    ) {
        val maskedBody = if (sensitiveJsonBodyFields.isNotEmpty()) {
            obfuscator
                .maskJsonBody(body, sensitiveJsonBodyFields)
        } else {
            body
        }
        val obfuscatedHeaders: HttpHeaders? = if (isHttpLogLevel(HttpLogLevel.HEADERS)) {
            obfuscator.obfuscateHeaders(request.headers, sensitiveHeaders)
        } else {
            null
        }
        val httpLog = HttpLog(
            type = HttpLogType.REQUEST,
            method = request.method,
            uri = uri,
            statusCode = null,
            headers = obfuscatedHeaders,
            body = maskedBody,
            source = Source.SERVER,
            durationMs = null
        )
        logger.info("HTTP Request: {}", logFormatter.format(httpLog))
    }

    private fun logResponse(
        response: ServerHttpResponse,
        uri: URI,
        method: HttpMethod,
        body: String,
        durationMs: Long
    ) {
        val maskedBody = if (sensitiveJsonBodyFields.isNotEmpty()) {
            obfuscator
                .maskJsonBody(body, sensitiveJsonBodyFields)
        } else {
            body
        }
        val obfuscatedHeaders: HttpHeaders? = if (isHttpLogLevel(HttpLogLevel.HEADERS)) {
            obfuscator.obfuscateHeaders(response.headers, sensitiveHeaders)
        } else {
            null
        }
        val httpLog = HttpLog(
            type = HttpLogType.RESPONSE,
            method = method,
            uri = uri,
            statusCode = response.statusCode?.value(),
            headers = obfuscatedHeaders,
            body = maskedBody,
            source = Source.SERVER,
            durationMs = durationMs
        )
        logger.info("HTTP Response: {}", logFormatter.format(httpLog))
    }

    private fun shouldNotLog(request: ServerHttpRequest): Boolean {
        val requestPath = request.uri.path
        val requestMethod = request.method
        return shouldNotLogPatterns.any { (method, pattern) ->
            (method == null || method == requestMethod) && pathMatcher.match(pattern, requestPath)
        }
    }

    private fun isHttpLogLevel(level: HttpLogLevel): Boolean {
        return httpLogLevel.ordinal >= level.ordinal
    }

    private fun maskUri(request: ServerHttpRequest): URI {
        val maskedUri = if (sensitiveParameters.isNotEmpty()) {
            obfuscator.maskParameters(request.uri, sensitiveParameters)
        } else {
            request.uri
        }
        return maskedUri
    }

    companion object {
        fun builder(
            logFormatter: LogFormatter,
            obfuscator: Obfuscator
        ): Builder {
            return Builder(logFormatter, obfuscator)
        }
    }

    class Builder(
        private val logFormatter: LogFormatter,
        private val obfuscator: Obfuscator
    ) {
        private var httpLogLevel: HttpLogLevel = HttpLogLevel.FULL
        private val sensitiveHeaders: MutableList<String> =
            mutableListOf("Authorization", "Cookie", "Set-Cookie")
        private val shouldNotLogPatterns: MutableList<Pair<HttpMethod?, String>> = mutableListOf()
        private val sensitiveJsonBodyFields: MutableList<String> = mutableListOf(
            "access_token",
            "refresh_token"
        )
        private val sensitiveParameters: MutableList<String> = mutableListOf(
            "access_token"
        )

        fun httpLogLevel(level: HttpLogLevel) = apply { this.httpLogLevel = level }
        fun sensitiveHeader(vararg headers: String) = apply { this.sensitiveHeaders.addAll(headers) }
        fun shouldNotLog(method: HttpMethod?, vararg patterns: String) = apply {
            patterns.forEach { this.shouldNotLogPatterns.add(Pair(method, it)) }
        }
        fun shouldNotLog(vararg patterns: String) = apply {
            this.shouldNotLogPatterns.addAll(patterns.map { Pair(null, it) })
        }
        fun sensitiveJsonBodyField(vararg paths: String) = apply {
            this.sensitiveJsonBodyFields.addAll(paths)
        }

        fun sensitiveParameter(vararg params: String) = apply {
            this.sensitiveParameters.addAll(params)
        }

        fun build(): LoggingFilter {
            return LoggingFilter(
                logFormatter,
                obfuscator,
                httpLogLevel,
                sensitiveHeaders,
                shouldNotLogPatterns,
                sensitiveJsonBodyFields,
                sensitiveParameters
            )
        }
    }
}
