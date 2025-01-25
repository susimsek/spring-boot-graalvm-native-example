package io.github.susimsek.springbootgraalvmnativeexample.config.logging.formatter

import io.github.susimsek.springbootgraalvmnativeexample.config.logging.model.HttpLog

interface LogFormatter {
    fun format(httpLog: HttpLog): String
}
