package com.example.bookcatalog.controllers;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.exception.BookNotFoundException;
import com.example.bookcatalog.exception.GlobalExceptionHandler;
import com.example.bookcatalog.model.PaginatedBooks;
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

        BookDto request = new BookDto(null, "Clean Code", "Robert C. Martin", BigDecimal.valueOf(45));
        BookDto response = new BookDto(1L, "Clean Code", "Robert C. Martin", BigDecimal.valueOf(45));

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
                .bodyValue(new BookDto(null,"A","B",BigDecimal.TEN))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldReturn400WhenInvalidRequest() {

        BookDto invalid = new BookDto(null,"","",BigDecimal.valueOf(-5));

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
                .bodyValue(new BookDto(null,"A","B",BigDecimal.TEN))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // =============================
    // GET ALL (PAGINATED)
    // =============================

    @Test
    void shouldReturnPaginatedBooks() {

        BookDto dto = new BookDto(1L,"Title","Author",BigDecimal.TEN);

        PaginatedBooks page = new PaginatedBooks(
                1L,
                List.of(dto)
        );

        when(bookService.getAll(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Mono.just(page));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .queryParam("title", "")
                        .queryParam("author", "")
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

        BookDto dto = new BookDto(1L,"Title","Author",BigDecimal.TEN);

        when(bookService.getById(1L))
                .thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void shouldReturn404WhenBookNotFound() {

        when(bookService.getById(1L))
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

        BookDto request = new BookDto(null,"Updated","Author",BigDecimal.TEN);
        BookDto response = new BookDto(1L,"Updated","Author",BigDecimal.TEN);

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
                .bodyValue(new BookDto(null,"A","B",BigDecimal.TEN))
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
}