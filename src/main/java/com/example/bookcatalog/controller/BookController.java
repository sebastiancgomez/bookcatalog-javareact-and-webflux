package com.example.bookcatalog.controller;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.model.PaginatedBooks;
import com.example.bookcatalog.services.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @PostMapping
    public Mono<BookDto> createBook(@Valid @RequestBody BookDto book) {
        log.info("POST /books - request: {}", book);
        return service.create(book)
                .doOnSuccess(b -> log.info("POST /books - response: {}", b));
    }

    @GetMapping("/{id}")
    public Mono<BookDto> getBookById(@PathVariable Long id) {
        log.info("GET /books/{}", id);
        return service.getById(id)
                .doOnSuccess(b -> log.info("GET /books/{} - response: {}", id, b));
    }

    @PutMapping("/{id}")
    public Mono<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto updatedBook) {
        log.info("PUT /books/{} - request: {}", id, updatedBook);
        return service.update(id, updatedBook)
                .doOnSuccess(b -> log.info("PUT /books/{} - response: {}", id, b));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteBook(@PathVariable Long id) {
        log.info("DELETE /books/{}", id);
        return service.delete(id)
                .doOnSuccess(v -> log.info("DELETE /books/{} - deleted", id));
    }

    @GetMapping
    public Mono<PaginatedBooks> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {

        return service.getAll(page, size, title, author);
    }
}