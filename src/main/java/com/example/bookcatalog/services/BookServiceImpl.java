package com.example.bookcatalog.services;

import com.example.bookcatalog.dto.BookDto;
import com.example.bookcatalog.exception.BookNotFoundException;
import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.model.PaginatedBooks;
import com.example.bookcatalog.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    // Mapper
    public BookDto toDto(Book book) {
        return new BookDto(book.getId(), book.getTitle(), book.getAuthor(), book.getPrice());
    }

    public Book toEntity(BookDto dto) {
        return new Book(dto.getId(), dto.getTitle(), dto.getAuthor(), dto.getPrice());
    }

    @Override
    public Mono<PaginatedBooks> getAll(int page, int size, String title, String author) {
        PageRequest pageable = PageRequest.of(page, size);

        Mono<Long> total = repository.countFiltered(title, author);
        Flux<Book> books = repository.findFiltered(title, author, pageable);

        return total.zipWith(books.collectList(),
                (t, list) -> new PaginatedBooks(t, list.stream().map(this::toDto).toList()));
    }

    @Override
    public Mono<BookDto> getById(Long id) {
        log.info("Buscando libro con ID: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
                .doOnSuccess(book -> log.info("Libro encontrado: {}", book))
                .doOnError(e -> log.error("Error buscando libro con ID {}: {}", id, e.getMessage()))
                .map(this::toDto);
    }

    @Override
    public Mono<BookDto> create(BookDto bookDto) {
        log.info("Creando libro: {}", bookDto);
        return repository.save(toEntity(bookDto))
                .doOnSuccess(saved -> log.info("Libro creado con ID: {}", saved.getId()))
                .doOnError(e -> log.error("Error creando libro: {}", e.getMessage(), e))
                .map(this::toDto);
    }

    @Override
    public Mono<BookDto> update(Long id, BookDto bookDto) {
        log.info("Actualizando libro ID {} con datos: {}", id, bookDto);
        return getById(id)
                .flatMap(existing -> {
                    existing.setTitle(bookDto.getTitle());
                    existing.setAuthor(bookDto.getAuthor());
                    existing.setPrice(bookDto.getPrice());
                    return repository.save(toEntity(existing));
                })
                .doOnSuccess(updated -> log.info("Libro actualizado: {}", updated))
                .doOnError(e -> log.error("Error actualizando libro ID {}: {}", id, e.getMessage()))
                .map(this::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.info("Deleting book with id={}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
                .flatMap(repository::delete)
                .doOnSuccess(v -> log.info("Libro eliminado con ID: {}", id))
                .doOnError(e -> log.error("Error eliminando libro ID {}: {}", id, e.getMessage()));
    }


}