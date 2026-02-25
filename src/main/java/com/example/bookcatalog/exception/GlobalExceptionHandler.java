package com.example.bookcatalog.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Map<String, Object>> handleBookNotFound(WebExchangeBindException ex) {

        String errorMessage = ex.getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return Mono.just(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "message", ex.getMessage()
        ));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {

        return Mono.just(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Conflict",
                "message", "Database integrity violation"
        ));
    }
}