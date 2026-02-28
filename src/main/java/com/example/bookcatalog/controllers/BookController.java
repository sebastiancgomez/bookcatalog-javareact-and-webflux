package com.example.bookcatalog.controllers;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.exception.ErrorResponse;
import com.example.bookcatalog.model.PaginatedBooks;
import com.example.bookcatalog.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Operations related to books")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    // =========================
    // CREATE
    // =========================
    @Operation(summary = "Create a new book")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Book created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Book already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
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
    @Operation(summary = "Get book by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
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
    @Operation(summary = "Update book by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
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
    @Operation(summary = "Delete book by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Book deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
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
    @Operation(summary = "Get all books (paginated)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Books retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaginatedBooks.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
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