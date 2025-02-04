package io.github.susimsek.springbootgraalvmnativeexample.config.logging

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.filter.LoggingFilter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.filter.WebClientLoggingFilter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter.JsonLogFormatter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter.LogFormatter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod

@Configuration(proxyBeanMethods = false)
class LoggingConfig {

    @Bean
    fun logFormatter(objectMapper: ObjectMapper): LogFormatter {
        return JsonLogFormatter(objectMapper)
    }

    @Bean
    fun loggingExchangeFilterFunction(logFormatter: LogFormatter): WebClientLoggingFilter {
        return WebClientLoggingFilter(logFormatter)
            .shouldNotLog(HttpMethod.GET, "/todos")
    }

    @Bean
    fun loggingFilter(logFormatter: LogFormatter): LoggingFilter {
        return LoggingFilter(logFormatter)
            .shouldNotLog(
                "/webjars/**",
                "/css/**",
                "/js/**",
                "/images/**",
                "/*.html",
                "/_next/**",
                "/*.js",
                "/*.css",
                "/*.ico",
                "/*.png",
                "/*.svg",
                "/*.webapp"
            )
            .shouldNotLog("/actuator/**")
            .shouldNotLog(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            )
    }
}
