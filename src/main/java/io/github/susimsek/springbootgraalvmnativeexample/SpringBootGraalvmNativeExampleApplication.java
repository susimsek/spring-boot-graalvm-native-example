package io.github.susimsek.springbootgraalvmnativeexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the application.
 *
 * <p>This class initializes and runs the Spring Boot application.</p>
 *
 * <p>It is annotated with {@link SpringBootApplication}, which enables auto-configuration,
 * component scanning, and additional Spring Boot configuration.</p>
 */
@SpringBootApplication
public class SpringBootGraalvmNativeExampleApplication {

    /**
     * Main method that serves as the entry point of the application.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringBootGraalvmNativeExampleApplication.class, args);
    }

}
