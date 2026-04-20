package com.pbl.stringprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application class for String Processing demonstration.
 *
 * This application demonstrates string manipulation using:
 * - StringBuffer: A mutable, thread-safe sequence of characters
 *   (append, insert, delete, reverse, replace operations)
 * - StringTokenizer: Breaks a string into tokens using delimiters
 *   (split, count, iterate over tokens)
 *
 * @SpringBootApplication combines:
 * - @Configuration: Marks this class as a source of bean definitions
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
 * - @ComponentScan: Enables component scanning in the current package
 */
@SpringBootApplication
public class StringProcessingApplication {

    public static void main(String[] args) {
        // Starts the Spring Boot application
        SpringApplication.run(StringProcessingApplication.class, args);
    }
}
