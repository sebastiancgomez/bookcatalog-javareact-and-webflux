package com.example.bookcatalog.services;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.dto.response.PaginatedBooks;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface BookService {
    Mono<BookDto> create(BookDto book);
    Mono<BookDto> update(Long id, BookDto book);
    Mono<Void> delete(Long id);
    Mono<PaginatedBooks> getAll(
            int page,
            int size,
            String title,
            String author,
            LocalDate from,
            LocalDate to,
            String dto);

    Mono<Object> getById(Long id, String dto);
}