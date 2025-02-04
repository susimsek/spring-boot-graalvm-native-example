package io.github.susimsek.springbootgraalvmnativeexample.config.logging.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.http.HttpHeaders
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

class Obfuscator(
    private val objectMapper: ObjectMapper
) {

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

    fun maskJsonBody(body: String, sensitivePaths: List<String>): String {
        return try {
            val root = objectMapper.readTree(body)
            sensitivePaths.forEach { path ->
                val keys = path.split(".")
                maskPath(root, keys)
            }
            objectMapper.writeValueAsString(root)
        } catch (e: IOException) {
            body
        }
    }

    fun maskParameters(uri: URI, sensitiveParameters: List<String>): URI {
        return try {
            val query = uri.query ?: return uri
            val maskedQuery = query.split("&").joinToString("&") { param ->
                val parts = param.split("=")
                if (parts.size == 2) {
                    val key = parts[0]
                    if (sensitiveParameters.any { it.equals(key, ignoreCase = true) }) {
                        "$key=******"
                    } else {
                        param
                    }
                } else {
                    param
                }
            }
           return URI(uri.scheme, uri.authority, uri.path, maskedQuery, uri.fragment)
        } catch (ex: URISyntaxException) {
            uri
        }
    }

    private fun maskPath(node: JsonNode, keys: List<String>) {
        if (keys.isEmpty()) return
        val currentKey = keys.first()
        if (node.isObject) {
            val obj = node as ObjectNode
            if (keys.size == 1) {
                if (obj.has(currentKey)) {
                    obj.put(currentKey, "******")
                }
            } else {
                val child = obj.get(currentKey)
                if (child != null) {
                    maskPath(child, keys.drop(1))
                }
            }
        } else if (node.isArray) {
            node.forEach { element ->
                maskPath(element, keys)
            }
        }
    }
}
