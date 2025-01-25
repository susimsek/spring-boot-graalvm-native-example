package io.github.susimsek.springbootgraalvmnativeexample.config.logging

import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogType
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.Source
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter.LogFormatter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.model.HttpLog
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.wrapper.BufferingClientHttpRequest
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.util.StopWatch
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

class WebClientLoggingFilter(private val logFormatter: LogFormatter) : ExchangeFilterFunction {

    private val logger = LoggerFactory.getLogger(WebClientLoggingFilter::class.java)

    override fun filter(request: ClientRequest, next: ExchangeFunction) = mono {
        coFilter(request, next)
    }

    private suspend fun coFilter(request: ClientRequest, next: ExchangeFunction): ClientResponse {
        val stopWatch = StopWatch()
        var requestBody: ByteArray? = null
        var responseBody: ByteArray? = null
        stopWatch.start()
        return next
            .exchange(
                ClientRequest
                    .from(request)
                    .body { outputMessage, context ->
                        BufferingClientHttpRequest(outputMessage).let { bufferingRequest ->
                            request.body().insert(bufferingRequest, context)
                                .doOnSuccess {
                                    requestBody = bufferingRequest.requestBody
                                }
                        }
                    }
                    .build()
            )
            .flatMap { response ->
                stopWatch.stop()
                val durationMs = stopWatch.totalTimeMillis
                logRequest(
                    request,
                    requestBody?.let { String(it, StandardCharsets.UTF_8) } ?: "",
                )
                val clientResponse = response.mutate().build()
                Mono.just(response)
                    .flatMap {
                        val responseHeaders = response.headers().asHttpHeaders()
                        if (responseHeaders.contentLength > 0 ||
                            responseHeaders.containsKey(HttpHeaders.TRANSFER_ENCODING)
                        ) {
                            response.bodyToMono(ByteArray::class.java)
                                .doOnNext { responseBody = it }
                                .map { b ->
                                    response.mutate().body(
                                        Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(b))
                                    ).build()
                                }
                                .switchIfEmpty(Mono.just(response))
                        } else {
                            Mono.just(response)
                        }
                    }
                    .doOnNext {
                        logResponse(
                            clientResponse,
                            request,
                            responseBody?.let { String(it, StandardCharsets.UTF_8) } ?: "",
                            durationMs
                        )
                    }
            }.awaitSingle()
    }

    private fun logRequest(request: ClientRequest, body: String) {
        val logBuilder = HttpLog(
            type = HttpLogType.REQUEST,
            method = request.method(),
            uri = request.url(),
            statusCode = null,
            headers = request.headers(),
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
            headers = response.headers().asHttpHeaders(),
            body = body,
            source = Source.CLIENT,
            durationMs = durationMs
        )
        logger.info("HTTP Response: {}", logFormatter.format(logBuilder))
    }
}
