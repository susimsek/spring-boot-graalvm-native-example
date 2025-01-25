package io.github.susimsek.springbootgraalvmnativeexample.config.logging.model

import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogType
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.Source
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import java.net.URI

data class HttpLog(
    var type: HttpLogType,
    var method: HttpMethod,
    var uri: URI,
    var statusCode: Int?,
    var headers: HttpHeaders?,
    var body: String? = null,
    var source: Source,
    var durationMs: Long?
)
