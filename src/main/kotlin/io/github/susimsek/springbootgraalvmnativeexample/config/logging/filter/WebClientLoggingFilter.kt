package io.github.susimsek.springbootgraalvmnativeexample.config.logging.filter

import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogLevel
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogType
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.Source
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter.LogFormatter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.model.HttpLog
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.utils.Obfuscator
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

class WebClientLoggingFilter private constructor(
    private val logFormatter: LogFormatter,
    private val obfuscator: Obfuscator,
    private val httpLogLevel: HttpLogLevel,
    private val sensitiveHeaders: List<String>,
    private val shouldNotLogPatterns: List<Pair<HttpMethod?, String>>,
    private val sensitiveJsonBodyFields: List<String>,
    private val sensitiveParameters: List<String>
) : ExchangeFilterFunction {

    private val logger = LoggerFactory.getLogger(WebClientLoggingFilter::class.java)

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

        logResponse(response, request, responseBody?.toString(StandardCharsets.UTF_8) ?: "", durationMs)
        return mutatedResponse
    }

    private fun logRequest(request: ClientRequest, body: String) {
        val maskedBody = if (sensitiveJsonBodyFields.isNotEmpty()) {
            obfuscator.maskJsonBody(body, sensitiveJsonBodyFields)
        } else {
            body
        }
        val maskedUri = if (sensitiveParameters.isNotEmpty()) {
            obfuscator.maskParameters(request.url(), sensitiveParameters)
        } else {
            request.url()
        }
        val obfuscatedHeaders: HttpHeaders? = if (isHttpLogLevel(HttpLogLevel.HEADERS)) {
            obfuscator.obfuscateHeaders(request.headers(), sensitiveHeaders)
        } else {
            null
        }
        val httpLog = HttpLog(
            type = HttpLogType.REQUEST,
            method = request.method(),
            uri = maskedUri,
            statusCode = null,
            headers = obfuscatedHeaders,
            body = maskedBody,
            source = Source.CLIENT,
            durationMs = null
        )
        logger.info("HTTP Request: {}", logFormatter.format(httpLog))
    }

    private fun logResponse(response: ClientResponse, request: ClientRequest, body: String, durationMs: Long) {
        val maskedBody = if (sensitiveJsonBodyFields.isNotEmpty()) {
            obfuscator.maskJsonBody(body, sensitiveJsonBodyFields)
        } else {
            body
        }
        val maskedUri = if (sensitiveParameters.isNotEmpty()) {
            obfuscator.maskParameters(request.url(), sensitiveParameters)
        } else {
            request.url()
        }
        val obfuscatedHeaders: HttpHeaders? = if (isHttpLogLevel(HttpLogLevel.HEADERS)) {
            obfuscator.obfuscateHeaders(response.headers().asHttpHeaders(), sensitiveHeaders)
        } else {
            null
        }
        val httpLog = HttpLog(
            type = HttpLogType.RESPONSE,
            method = request.method(),
            uri = maskedUri,
            statusCode = response.statusCode().value(),
            headers = obfuscatedHeaders,
            body = maskedBody,
            source = Source.CLIENT,
            durationMs = durationMs
        )
        logger.info("HTTP Response: {}", logFormatter.format(httpLog))
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

    private fun isHttpLogLevel(level: HttpLogLevel): Boolean {
        return httpLogLevel.ordinal >= level.ordinal
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
        private val sensitiveHeaders: MutableList<String> = mutableListOf(
          "Authorization", "Cookie", "Set-Cookie")
        private val shouldNotLogPatterns: MutableList<Pair<HttpMethod?, String>> = mutableListOf()

        private val sensitiveJsonBodyFields: MutableList<String> =
            mutableListOf("access_token", "refresh_token")

        private val sensitiveParameters: MutableList<String> = mutableListOf(
            "access_token"
        )

        fun httpLogLevel(level: HttpLogLevel) = apply { this.httpLogLevel = level }
        fun sensitiveHeader(vararg headers: String) = apply { this.sensitiveHeaders.addAll(headers) }
        fun sensitiveJsonBodyField(vararg fields: String) = apply {
            this.sensitiveJsonBodyFields.addAll(fields)
        }
        fun sensitiveParameter(vararg params: String) = apply {
            this.sensitiveParameters.addAll(params)
        }
        fun shouldNotLog(method: HttpMethod?, vararg patterns: String) = apply {
            patterns.forEach { this.shouldNotLogPatterns.add(Pair(method, it)) }
        }
        fun shouldNotLog(vararg patterns: String) = apply {
            this.shouldNotLogPatterns.addAll(patterns.map { Pair(null, it) })
        }

        fun build(): WebClientLoggingFilter {
            return WebClientLoggingFilter(
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
