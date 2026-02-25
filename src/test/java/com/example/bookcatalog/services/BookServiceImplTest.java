package com.example.bookcatalog.services;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.PaginatedBooks;
import com.example.bookcatalog.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    private BookRepository repository;
    private BookServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BookRepository.class);
        service = new BookServiceImpl(repository);
    }

    @Test
    void testCreateBook() {
        Book book = new Book(null, "Title 1", "Author 1", BigDecimal.valueOf(20));
        BookDto dto = service.toDto(book);

        when(repository.save(any(Book.class))).thenReturn(Mono.just(book));

        StepVerifier.create(service.create(dto))
                .expectNextMatches(b -> b.getTitle().equals("Title 1"))
                .verifyComplete();

        verify(repository, times(1)).save(any(Book.class));
    }

    @Test
    void testGetById() {
        Book book = new Book(1L, "Title 1", "Author 1", BigDecimal.valueOf(20));
        when(repository.findById(1L)).thenReturn(Mono.just(book));

        StepVerifier.create(service.getById(1L))
                .expectNextMatches(b -> b.getTitle().equals("Title 1"))
                .verifyComplete();

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testUpdateBook() {
        Book existing = new Book(1L, "Old Title", "Old Author", BigDecimal.valueOf(15));
        BookDto updatedDto = new BookDto(1L, "New Title", "New Author", BigDecimal.valueOf(25));
        when(repository.findById(1L)).thenReturn(Mono.just(existing));
        when(repository.save(any(Book.class))).thenReturn(Mono.just(existing));

        StepVerifier.create(service.update(1L, updatedDto))
                .expectNextMatches(b -> b.getTitle().equals("New Title") &&
                        b.getPrice().equals(BigDecimal.valueOf(25)))
                .verifyComplete();

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Book.class));
    }

    @Test
    void testDeleteBook() {
        Book book = new Book(1L, "Title 1", "Author 1", BigDecimal.valueOf(20));
        when(repository.findById(1L)).thenReturn(Mono.just(book));
        when(repository.delete(book)).thenReturn(Mono.empty());

        StepVerifier.create(service.delete(1L))
                .verifyComplete();

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(book);
    }

    @Test
    void testGetAllPaginatedWithFilters() {
        Book book1 = new Book(1L, "Java Basics", "John Doe", BigDecimal.valueOf(20));
        Book book2 = new Book(2L, "Spring Boot", "Jane Doe", BigDecimal.valueOf(30));

        when(repository.countFiltered("Java", "John")).thenReturn(Mono.just(1L));
        when(repository.findFiltered("Java", "John", PageRequest.of(0, 5)))
                .thenReturn(Flux.just(book1));

        StepVerifier.create(service.getAll(0, 5, "Java", "John"))
                .expectNextMatches(paginated ->
                        paginated.getTotal() == 1 &&
                                paginated.getBooks().size() == 1 &&
                                paginated.getBooks().get(0).getTitle().equals("Java Basics")
                )
                .verifyComplete();

        verify(repository, times(1)).countFiltered("Java", "John");
        verify(repository, times(1)).findFiltered("Java", "John", PageRequest.of(0, 5));
    }

    @Test
    void testGetAllPaginatedNoFilters() {
        Book book1 = new Book(1L, "Book1", "Author1", BigDecimal.valueOf(10));
        Book book2 = new Book(2L, "Book2", "Author2", BigDecimal.valueOf(15));

        when(repository.countFiltered(null, null)).thenReturn(Mono.just(2L));
        when(repository.findFiltered(null, null, PageRequest.of(0, 5)))
                .thenReturn(Flux.just(book1, book2));

        StepVerifier.create(service.getAll(0, 5, null, null))
                .expectNextMatches(paginated ->
                        paginated.getTotal() == 2 &&
                                paginated.getBooks().size() == 2 &&
                                paginated.getBooks().get(0).getTitle().equals("Book1") &&
                                paginated.getBooks().get(1).getTitle().equals("Book2")
                )
                .verifyComplete();

        verify(repository, times(1)).countFiltered(null, null);
        verify(repository, times(1)).findFiltered(null, null, PageRequest.of(0, 5));
    }
}