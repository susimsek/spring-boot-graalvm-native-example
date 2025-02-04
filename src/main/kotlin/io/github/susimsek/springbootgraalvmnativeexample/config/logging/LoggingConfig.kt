package io.github.susimsek.springbootgraalvmnativeexample.config.logging

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.enums.HttpLogLevel
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.filter.LoggingFilter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.filter.WebClientLoggingFilter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter.JsonLogFormatter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter.LogFormatter
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.utils.Obfuscator
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
    fun obfuscator(objectMapper: ObjectMapper): Obfuscator {
        return Obfuscator(objectMapper)
    }

    @Bean
    fun loggingExchangeFilterFunction(
        logFormatter: LogFormatter,
        obfuscator: Obfuscator
    ): WebClientLoggingFilter {
        return WebClientLoggingFilter.builder(logFormatter, obfuscator)
            .httpLogLevel(HttpLogLevel.FULL)
            .shouldNotLog(HttpMethod.GET, "/todos")
            .build()
    }

    @Bean
    fun loggingFilter(
        logFormatter: LogFormatter,
        obfuscator: Obfuscator
    ): LoggingFilter {
        return LoggingFilter.builder(logFormatter, obfuscator)
            .httpLogLevel(HttpLogLevel.FULL)
            .shouldNotLog(
                "/webjars/**",
                "/css/**",
                "/",
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
            ).build()
    }
}
