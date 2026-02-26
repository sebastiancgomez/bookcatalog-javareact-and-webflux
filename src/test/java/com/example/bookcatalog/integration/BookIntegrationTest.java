package com.example.bookcatalog.integration;

import com.example.bookcatalog.dto.BookDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.Objects;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static Long createdBookId;

    // ==========================
    // CREATE
    // ==========================

    @Test
    @Order(1)
    void shouldCreateBook() {

        BookDto request = new BookDto(
                null,
                "Integration Book",
                "Tester",
                BigDecimal.valueOf(99.99)
        );

        createdBookId = Objects.requireNonNull(webTestClient.post()
                        .uri("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(BookDto.class)
                        .returnResult()
                        .getResponseBody())
                .getId();
    }

    // ==========================
    // GET BY ID
    // ==========================

    @Test
    @Order(2)
    void shouldReturnCreatedBook() {

        webTestClient.get()
                .uri("/books/{id}", createdBookId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Integration Book");
    }

    // ==========================
    // DELETE
    // ==========================

    @Test
    @Order(3)
    void shouldDeleteBook() {

        webTestClient.delete()
                .uri("/books/{id}", createdBookId)
                .exchange()
                .expectStatus().isNoContent();
    }

    // ==========================
    // VERIFY DELETE
    // ==========================

    @Test
    @Order(4)
    void shouldReturn404AfterDelete() {

        webTestClient.get()
                .uri("/books/{id}", createdBookId)
                .exchange()
                .expectStatus().isNotFound();
    }
    @BeforeEach
    void cleanup() {
        webTestClient.delete().uri("/books/cleanup").exchange();
    }
}