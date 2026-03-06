package com.example.bookcatalog.services;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.dto.BookFullDto;
import com.example.bookcatalog.exception.BookNotFoundException;
import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;


import static org.mockito.ArgumentMatchers.any;
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

        StepVerifier.create(service.getById(1L, "full"))
                .expectNextMatches(b -> {
                    BookFullDto dto = (BookFullDto) b;
                    return dto.getTitle().equals("Title 1");
                })
                .verifyComplete();

        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testUpdateBook() {

        Book existing = new Book(1L, "Old Title", "Old Author", BigDecimal.valueOf(15));
        Book updatedEntity = new Book(1L, "New Title", "New Author", BigDecimal.valueOf(25));

        BookDto updatedDto = new BookDto(
                1L,
                "New Title",
                "New Author",
                BigDecimal.valueOf(25),
                LocalDate.now()
        );

        when(repository.findById(1L)).thenReturn(Mono.just(existing));
        when(repository.save(any(Book.class))).thenReturn(Mono.just(updatedEntity));

        StepVerifier.create(service.update(1L, updatedDto))
                .expectNextMatches(dto ->
                        dto.getTitle().equals("New Title") &&
                                dto.getPrice().equals(BigDecimal.valueOf(25))
                )
                .verifyComplete();

        verify(repository).findById(1L);
        verify(repository).save(any(Book.class));
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

        when(repository.countFiltered("Java", "John", null, null)).thenReturn(Mono.just(1L));
        when(repository.findFiltered("Java", "John", null, null, PageRequest.of(0, 5)))
                .thenReturn(Flux.just(book1));

        StepVerifier.create(service.getAll(0, 5, "Java", "John", null, null, "full"))
                .expectNextMatches(paginated -> {
                    BookFullDto b1 = (BookFullDto) paginated.getBooks().get(0);
                   return paginated.getTotal() == 1 &&
                            paginated.getBooks().size() == 1 &&
                            b1.getTitle().equals("Java Basics");
                })
                .verifyComplete();

        verify(repository, times(1)).countFiltered("Java", "John", null, null);
        verify(repository, times(1)).findFiltered("Java", "John", null, null, PageRequest.of(0, 5));
    }

    @Test
    void testGetAllPaginatedNoFilters() {
        Book book1 = new Book(1L, "Book1", "Author1", BigDecimal.valueOf(10));
        Book book2 = new Book(2L, "Book2", "Author2", BigDecimal.valueOf(15));

        when(repository.countFiltered(null, null, null, null)).thenReturn(Mono.just(2L));
        when(repository.findFiltered(null, null, null, null, PageRequest.of(0, 5)))
                .thenReturn(Flux.just(book1, book2));

        StepVerifier.create(service.getAll(0, 5, null, null, null, null, "full"))
                .expectNextMatches(paginated ->{

                    BookFullDto b1 = (BookFullDto) paginated.getBooks().get(0);
                    BookFullDto b2 = (BookFullDto) paginated.getBooks().get(1);

                    return paginated.getTotal() == 2 &&
                            paginated.getBooks().size() == 2 &&
                            b1.getTitle().equals("Book1") &&
                            b2.getTitle().equals("Book2");
                })
                .verifyComplete();

        verify(repository, times(1)).countFiltered(null, null, null, null);
        verify(repository, times(1)).findFiltered(null, null, null, null, PageRequest.of(0, 5));
    }
    @Test
    void testGetByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.getById(1L, "full"))
                .expectError(BookNotFoundException.class)
                .verify();

        verify(repository, times(1)).findById(1L);
    }
    @Test
    void testUpdateBookNotFound() {
        BookDto updatedDto = new BookDto(1L, "New Title", "New Author", BigDecimal.valueOf(25), LocalDate.now());

        when(repository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.update(1L, updatedDto))
                .expectError(BookNotFoundException.class)
                .verify();

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any());
    }
    @Test
    void testDeleteBookNotFound() {
        when(repository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.delete(1L))
                .expectError(BookNotFoundException.class)
                .verify();

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).delete(any());
    }
    @Test
    void testCreateBookDatabaseError() {
        Book book = new Book(null, "Title 1", "Author 1", BigDecimal.valueOf(20));
        BookDto dto = service.toDto(book);

        when(repository.save(any(Book.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(service.create(dto))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository, times(1)).save(any(Book.class));
    }
    @Test
    void testGetAllWithDateRange() {

        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        Book book = new Book(1L, "Java 2024", "Author", BigDecimal.TEN);

        when(repository.countFiltered(null, null, from, to))
                .thenReturn(Mono.just(1L));

        when(repository.findFiltered(null, null, from, to, PageRequest.of(0, 5)))
                .thenReturn(Flux.just(book));

        StepVerifier.create(service.getAll(0, 5, null, null, from, to, "full"))
                .expectNextMatches(p ->
                        p.getTotal() == 1 &&
                                p.getBooks().size() == 1
                )
                .verifyComplete();

        verify(repository).countFiltered(null, null, from, to);
        verify(repository).findFiltered(null, null, from, to, PageRequest.of(0, 5));
    }
    @Test
    void testGetAllFromAfterToShouldFail() {

        LocalDate from = LocalDate.of(2024, 2, 1);
        LocalDate to = LocalDate.of(2024, 1, 1);

        StepVerifier.create(service.getAll(0, 5, null, null, from, to, "full"))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never()).countFiltered(any(), any(), any(), any());
    }

}