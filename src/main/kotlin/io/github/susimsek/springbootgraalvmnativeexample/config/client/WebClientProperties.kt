package io.github.susimsek.springbootgraalvmnativeexample.config.client

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "spring.webclient")
data class WebClientProperties(
  var connectTimeout: Duration? = Duration.ofSeconds(5),
  var readTimeout: Duration? = Duration.ofSeconds(10),
    var clients: MutableMap<String, ClientConfig> = HashMap()
) {
    data class ClientConfig(
        var url: String? = null
    )
}
