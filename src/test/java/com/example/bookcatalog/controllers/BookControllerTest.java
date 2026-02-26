package com.example.bookcatalog.controllers;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.exception.GlobalExceptionHandler;
import com.example.bookcatalog.services.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(BookController.class)
@Import(GlobalExceptionHandler.class) // importante si está en otro paquete
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    // =============================
    // CASO POSITIVO
    // =============================

    @Test
    void shouldCreateBookSuccessfully() {

        BookDto request = new BookDto(
                null,
                "Clean Code",
                "Robert C. Martin",
                BigDecimal.valueOf(45.00)
        );

        BookDto response = new BookDto(
                1L,
                "Clean Code",
                "Robert C. Martin",
                BigDecimal.valueOf(45.00)
        );

        when(bookService.create(any()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Clean Code");
    }

    // =============================
    // CASO NEGATIVO 409
    // =============================

    @Test
    void shouldReturn409WhenDuplicateBook() {

        BookDto request = new BookDto(
                null,
                "Duplicate",
                "Author",
                BigDecimal.valueOf(10.00)
        );

        when(bookService.create(any()))
                .thenReturn(Mono.error(
                        new DataIntegrityViolationException("Duplicate key")
                ));

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    // =============================
    // CASO NEGATIVO 400 (validación)
    // =============================

    @Test
    void shouldReturn400WhenRequestIsInvalid() {

        BookDto invalidRequest = new BookDto(
                null,
                "",  // título vacío
                "",
                BigDecimal.valueOf(-5) // precio inválido
        );

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // =============================
    // CASO NEGATIVO 500
    // =============================

    @Test
    void shouldReturn500WhenUnexpectedErrorOccurs() {

        BookDto request = new BookDto(
                null,
                "Test",
                "Author",
                BigDecimal.valueOf(20.00)
        );

        when(bookService.create(any()))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}