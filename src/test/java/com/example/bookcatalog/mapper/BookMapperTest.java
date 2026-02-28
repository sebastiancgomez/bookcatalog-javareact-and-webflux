package com.example.bookcatalog.mapper;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.model.Book;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookMapperTest {

    @Test
    public void shouldMapToDto() {
        Book book = new Book(1L, "Title", "Author", BigDecimal.TEN);

        BookDto dto = BookMapper.toDto(book);

        assertEquals(1L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Author", dto.getAuthor());
        assertEquals(BigDecimal.TEN, dto.getPrice());
    }

    @Test
    public void shouldMapToEntity() {
        BookDto dto = new BookDto(1L, "Title", "Author", BigDecimal.TEN);

        Book book = BookMapper.toEntity(dto);

        assertEquals(1L, book.getId());
        assertEquals("Title", book.getTitle());
        assertEquals("Author", book.getAuthor());
        assertEquals(BigDecimal.TEN, book.getPrice());
    }
}