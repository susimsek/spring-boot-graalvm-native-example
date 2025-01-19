package io.github.susimsek.springbootgraalvmnativeexample.exception

/**
 * Exception to be thrown when a validation error occurs.
 *
 * @param message the validation error message.
 */
class ValidationException(message: String) : RuntimeException(message)
