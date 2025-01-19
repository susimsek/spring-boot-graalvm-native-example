package io.github.susimsek.springbootgraalvmnativeexample.utils

object StringUtils {
    fun String.toSnakeCase(): String {
        return this.replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .replace(Regex("([A-Z])([A-Z][a-z])"), "$1_$2")
            .lowercase()
    }
}
