package io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.model.HttpLog
import org.springframework.http.HttpHeaders
import org.springframework.util.StringUtils
import java.io.IOException

class JsonLogFormatter(private val objectMapper: ObjectMapper) : LogFormatter {

    override fun format(httpLog: HttpLog): String {
        val logNode: ObjectNode = objectMapper.createObjectNode()
        logNode.put("source", httpLog.source.toString().lowercase())
        logNode.put("type", httpLog.type.toString().lowercase())
        logNode.put("method", httpLog.method.name())
        logNode.put("uri", httpLog.uri.toString())
        logNode.put("host", httpLog.uri.host)
        logNode.put("path", httpLog.uri.path)

        httpLog.durationMs?.let { logNode.put("duration", "${it}ms") }
        httpLog.statusCode?.let { logNode.put("statusCode", it) }
        httpLog.headers?.let { logNode.set<JsonNode>("headers", parseHeaders(it)) }

        if (StringUtils.hasText(httpLog.body)) {
            logNode.set<JsonNode>("body", parseBody(httpLog.body!!))
        }

        return logNode.toPrettyString()
    }

    private fun parseHeaders(headers: HttpHeaders): JsonNode {
        return objectMapper.valueToTree(headers)
    }

    private fun parseBody(bodyString: String): JsonNode {
        return try {
            objectMapper.readTree(bodyString)
        } catch (e: IOException) {
            val node = objectMapper.createObjectNode()
            node.put("body", bodyString) // Not a JSON body, log as plain text
            node
        }
    }
}
