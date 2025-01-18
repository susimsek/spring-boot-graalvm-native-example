package io.github.susimsek.springbootgraalvmnativeexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Entry point for the application.
 *
 * <p>This class initializes and runs the Spring Boot application.</p>
 *
 * <p>It is annotated with {@link SpringBootApplication}, which enables autoconfiguration,
 * component scanning, and additional Spring Boot configuration.</p>
 */
@SpringBootApplication
class SpringBootGraalvmNativeExampleApplication

/**
 * Main method that serves as the entry point of the application.
 *
 * @param args command-line arguments passed to the application.
 */
fun main(args: Array<String>) {
  runApplication<SpringBootGraalvmNativeExampleApplication>(*args)
}
