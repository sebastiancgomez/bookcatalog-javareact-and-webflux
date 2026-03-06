package com.example.bookcatalog.mapper;

import com.example.bookcatalog.dto.BookFullDto;
import com.example.bookcatalog.dto.BookMinimalDto;
import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.dto.BookDto;

public class BookMapper {

    public static BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getPublishDate()
        );
    }

    public static Book toEntity(BookDto dto) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPrice(dto.getPrice());
        book.setPublishDate(dto.getPublishDate());
        return book;
    }
    public static BookMinimalDto toMinimalDto(Book book) {
        return new BookMinimalDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor()
        );
    }

    public static BookFullDto toFullDto(Book book) {
        return new BookFullDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getPublishDate()
        );
    }
}