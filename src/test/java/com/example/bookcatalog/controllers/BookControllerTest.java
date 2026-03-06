package com.example.bookcatalog.controllers;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.dto.BookFullDto;
import com.example.bookcatalog.dto.response.PageResponse;
import com.example.bookcatalog.dto.response.PaginatedBooks;
import com.example.bookcatalog.exception.BookNotFoundException;
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
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    // =============================
    // POST
    // =============================

    @Test
    void shouldCreateBookSuccessfully() {

        BookDto request = new BookDto(null, "Clean Code", "Robert C. Martin", BigDecimal.valueOf(45), LocalDate.now() );
        BookDto response = new BookDto(1L, "Clean Code", "Robert C. Martin", BigDecimal.valueOf(45), LocalDate.now());

        when(bookService.create(any()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void shouldReturn409WhenDuplicateBook() {

        when(bookService.create(any()))
                .thenReturn(Mono.error(new DataIntegrityViolationException("Duplicate")));

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BookDto(null,"A","B",BigDecimal.TEN, LocalDate.now()))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldReturn400WhenInvalidRequest() {

        BookDto invalid = new BookDto(null,"","",BigDecimal.valueOf(-5), LocalDate.now());

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn500WhenUnexpectedError() {

        when(bookService.create(any()))
                .thenReturn(Mono.error(new RuntimeException("Unexpected")));

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BookDto(null,"A","B",BigDecimal.TEN, LocalDate.now()))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // =============================
    // GET ALL (PAGINATED)
    // =============================

    @Test
    void shouldReturnPaginatedBooks() {

        BookDto dto = new BookDto(
                1L,
                "Title",
                "Author",
                BigDecimal.TEN,
                LocalDate.now()
        );

                PaginatedBooks page = new PaginatedBooks(
                1L,
                List.of(dto)
        );
        when(bookService.getAll(
                anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                any(),
                any(),
                eq("full")
        )).thenReturn(Mono.just(page));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .queryParam("title", "")
                        .queryParam("author", "")
                        .queryParam("publishDateFrom", "")
                        .queryParam("publishDateTo", "")
                        .queryParam("dto","full")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.total").isEqualTo(1)
                .jsonPath("$.books[0].id").isEqualTo(1);
    }

    // =============================
    // GET BY ID
    // =============================

    @Test
    void shouldReturnBookById() {

        BookFullDto dto = new BookFullDto(
                1L,
                "Title",
                "Author",
                BigDecimal.TEN,
                LocalDate.now()
        );

        when(bookService.getById(1L, "full"))
                .thenReturn(Mono.<Object>just(dto));

        webTestClient.get()
                .uri("/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void shouldReturn404WhenBookNotFound() {

        when(bookService.getById(1L,"full"))
                .thenReturn(Mono.error(new BookNotFoundException(1L)));

        webTestClient.get()
                .uri("/books/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    // =============================
    // PUT
    // =============================

    @Test
    void shouldUpdateBook() {

        BookDto request = new BookDto(null,"Updated","Author",BigDecimal.TEN, LocalDate.now());
        BookDto response = new BookDto(1L,"Updated","Author",BigDecimal.TEN, LocalDate.now());

        when(bookService.update(eq(1L), any()))
                .thenReturn(Mono.just(response));

        webTestClient.put()
                .uri("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Updated");
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingBook() {

        when(bookService.update(eq(1L), any()))
                .thenReturn(Mono.error(new BookNotFoundException(1L)));

        webTestClient.put()
                .uri("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BookDto(null,"A","B",BigDecimal.TEN, LocalDate.now()))
                .exchange()
                .expectStatus().isNotFound();
    }

    // =============================
    // DELETE
    // =============================

    @Test
    void shouldDeleteBook() {

        when(bookService.delete(1L))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/books/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingBook() {

        when(bookService.delete(1L))
                .thenReturn(Mono.error(new BookNotFoundException(1L)));

        webTestClient.delete()
                .uri("/books/1")
                .exchange()
                .expectStatus().isNotFound();
    }
    @Test
    void shouldFilterByPublishDateFrom() {

        BookDto dto = new BookDto(
                1L,
                "Filtered Book",
                "Author",
                BigDecimal.TEN,
                LocalDate.of(2024, 1, 10)
        );

        PaginatedBooks page = new PaginatedBooks(
                1L,
                List.of(dto)
        );

        when(bookService.getAll(
                anyInt(),
                anyInt(),
                any(),
                any(),
                eq(LocalDate.of(2024, 1, 1)),
                isNull(),
                eq("full")
        )).thenReturn(Mono.just(page));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("publishDateFrom", "2024-01-01")
                        .queryParam("dto", "full")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.total").isEqualTo(1)
                .jsonPath("$.books[0].id").isEqualTo(1);
    }
    @Test
    void shouldReturn400WhenPublishDateFromAfterTo() {

        when(bookService.getAll(
                anyInt(),
                anyInt(),
                any(),
                any(),
                eq(LocalDate.of(2024, 2, 1)),
                eq(LocalDate.of(2024, 1, 1)),
                eq("full")
        )).thenReturn(Mono.error(
                new IllegalArgumentException("publishDateFrom must be before publishDateTo")
        ));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("publishDateFrom", "2024-02-01")
                        .queryParam("publishDateTo", "2024-01-01")
                        .queryParam("dto","full")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
    @Test
    void shouldReturn400WhenInvalidDateFormat() {

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("publishDateFrom", "invalid-date")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
}