package io.github.susimsek.springbootgraalvmnativeexample.config.logging.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogType
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.Source
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import java.net.URI

@JsonInclude(JsonInclude.Include.NON_NULL)
data class HttpLog(
    @JsonProperty
    var type: HttpLogType,

    @JsonProperty
    var method: HttpMethod,

    @JsonProperty
    var uri: URI,

    @JsonProperty
    var statusCode: Int?,

    @JsonProperty
    var headers: HttpHeaders?,

    @JsonProperty
    var body: String?,

    @JsonProperty
    var source: Source,

    @JsonProperty
    var durationMs: Long?
)
