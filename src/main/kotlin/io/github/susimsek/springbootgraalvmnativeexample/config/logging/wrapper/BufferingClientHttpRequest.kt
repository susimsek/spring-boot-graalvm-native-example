package io.github.susimsek.springbootgraalvmnativeexample.config.logging.wrapper

import io.github.susimsek.springbootgraalvmnativeexample.config.logging.utils.DataBufferCopyUtils
import org.reactivestreams.Publisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.http.client.reactive.ClientHttpRequestDecorator
import reactor.core.publisher.Mono

class BufferingClientHttpRequest(
    delegate: ClientHttpRequest
) : ClientHttpRequestDecorator(delegate) {

    var requestBody: ByteArray? = null
        private set

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        return super.writeWith(bufferingWrap(body))
    }

    private fun bufferingWrap(body: Publisher<out DataBuffer>): Publisher<out DataBuffer> {
        return DataBufferCopyUtils.wrapAndBuffer(body) { copiedBody ->
            this.requestBody = copiedBody
        }
    }
}
