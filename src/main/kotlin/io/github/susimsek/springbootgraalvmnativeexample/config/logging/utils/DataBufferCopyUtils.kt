package io.github.susimsek.springbootgraalvmnativeexample.config.logging.utils

import org.reactivestreams.Publisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory

object DataBufferCopyUtils {

    fun wrapAndBuffer(body: Publisher<out DataBuffer>, copyConsumer: (ByteArray) -> Unit): Publisher<out DataBuffer> {
        return DataBufferUtils
            .join(body)
            .defaultIfEmpty(DefaultDataBufferFactory.sharedInstance.wrap(ByteArray(0)))
            .map { dataBuffer ->
                val bytes = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(bytes)
                DataBufferUtils.release(dataBuffer)
                val wrappedDataBuffer: DefaultDataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(bytes)
                copyConsumer(bytes)
                wrappedDataBuffer
            }
    }
}
