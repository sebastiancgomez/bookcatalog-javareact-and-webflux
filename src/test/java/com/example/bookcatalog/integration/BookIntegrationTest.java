package com.example.bookcatalog.integration;

import com.example.bookcatalog.dto.BookDto;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class BookIntegrationTest extends PostgresContainerConfig {

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
                BigDecimal.valueOf(99.99),
                LocalDate.now()
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
    @Test
    void shouldFilterByPublishDateRange() {

        BookDto older = new BookDto(
                null,
                "Old Book",
                "Author",
                BigDecimal.TEN,
                LocalDate.of(2023, 1, 1)
        );

        BookDto newer = new BookDto(
                null,
                "New Book",
                "Author",
                BigDecimal.TEN,
                LocalDate.of(2024, 6, 1)
        );

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(older)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newer)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("publishDateFrom", "2024-01-01")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.total").isEqualTo(1)
                .jsonPath("$.books[0].title").isEqualTo("New Book");
    }
    @Test
    void shouldReturn400WhenFromAfterToIntegration() {

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("publishDateFrom", "2024-01-31")
                        .queryParam("publishDateTo", "2024-01-01")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
    @Autowired(required = false)
    private Flyway flyway;

    @Test
    void checkFlywayBean() {
        System.out.println("Flyway bean: " + flyway);
    }
    @BeforeEach
    void cleanup() {
        webTestClient.delete().uri("/books/cleanup").exchange();
    }
}