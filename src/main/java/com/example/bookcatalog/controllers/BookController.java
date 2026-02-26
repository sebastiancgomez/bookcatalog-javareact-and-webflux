package com.example.bookcatalog.controllers;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.model.PaginatedBooks;
import com.example.bookcatalog.services.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    // =========================
    // CREATE
    // =========================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDto> createBook(@Valid @RequestBody BookDto book) {

        log.info("POST /books - request: {}", book);

        return service.create(book)
                .doOnSuccess(saved ->
                        log.info("POST /books - created id={}", saved.getId())
                )
                .doOnError(error ->
                        log.error("POST /books - error creating book", error)
                );
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public Mono<BookDto> getBookById(@PathVariable Long id) {

        log.info("GET /books/{}", id);

        return service.getById(id)
                .doOnSuccess(book ->
                        log.info("GET /books/{} - found", id)
                )
                .doOnError(error ->
                        log.error("GET /books/{} - error", id, error)
                );
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    public Mono<BookDto> updateBook(@PathVariable Long id,
                                    @Valid @RequestBody BookDto updatedBook) {

        log.info("PUT /books/{} - request: {}", id, updatedBook);

        return service.update(id, updatedBook)
                .doOnSuccess(updated ->
                        log.info("PUT /books/{} - updated successfully", id)
                )
                .doOnError(error ->
                        log.error("PUT /books/{} - error updating book", id, error)
                );
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable Long id) {

        log.info("DELETE /books/{}", id);

        return service.delete(id)
                .doOnSuccess(v ->
                        log.info("DELETE /books/{} - deleted successfully", id)
                )
                .doOnError(error ->
                        log.error("DELETE /books/{} - error deleting book", id, error)
                );
    }

    // =========================
    // GET ALL (PAGINATED)
    // =========================
    @GetMapping
    public Mono<PaginatedBooks> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {

        log.info("GET /books - page={}, size={}, title={}, author={}",
                page, size, title, author);

        return service.getAll(page, size, title, author)
                .doOnError(error ->
                        log.error("GET /books - error retrieving books", error)
                );
    }
}