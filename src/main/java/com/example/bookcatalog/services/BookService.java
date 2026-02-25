package com.example.bookcatalog.services;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.model.PaginatedBooks;
import com.example.bookcatalog.model.Book;
import reactor.core.publisher.Mono;

public interface BookService {
    Mono<BookDto> getById(Long id);
    Mono<BookDto> create(BookDto book);
    Mono<BookDto> update(Long id, BookDto book);
    Mono<Void> delete(Long id);

    Mono<PaginatedBooks> getAll(int page, int size, String title, String author);
}