package com.example.bookcatalog.mapper;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.dto.BookDto;

public class BookMapper {

    public static BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice()
        );
    }

    public static Book toEntity(BookDto dto) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPrice(dto.getPrice());
        return book;
    }
}