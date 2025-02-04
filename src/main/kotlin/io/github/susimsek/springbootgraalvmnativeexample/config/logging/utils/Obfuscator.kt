package io.github.susimsek.springbootgraalvmnativeexample.config.logging.utils

import org.springframework.http.HttpHeaders

object Obfuscator {

    fun obfuscateHeaders(headers: HttpHeaders, sensitiveHeaders: List<String>): HttpHeaders {
        val masked = HttpHeaders()
        headers.forEach { (key, values) ->
            if (sensitiveHeaders.any { it.equals(key, ignoreCase = true) }) {
                masked[key] = listOf("******")
            } else {
                masked[key] = values
            }
        }
        return masked
    }
}
