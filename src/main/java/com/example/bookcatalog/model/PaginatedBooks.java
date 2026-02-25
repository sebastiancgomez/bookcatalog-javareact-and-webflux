package com.example.bookcatalog.model;

import com.example.bookcatalog.dto.BookDto;
import java.util.List;

public class PaginatedBooks {
    private final Long total;
    private final List<BookDto> books;

    public PaginatedBooks(Long total, List<BookDto> books) {
        this.total = total;
        this.books = books;
    }

    public Long getTotal() {
        return total;
    }

    public List<BookDto> getBooks() {
        return books;
    }
}