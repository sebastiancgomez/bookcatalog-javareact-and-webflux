package com.example.bookcatalog.repository;

import com.example.bookcatalog.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookRepository extends ReactiveCrudRepository<Book, Long> {

    @Query("SELECT * FROM book WHERE (:title IS NULL OR title ILIKE '%' || :title || '%') AND (:author IS NULL OR author ILIKE '%' || :author || '%') ORDER BY id LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}")
    Flux<Book> findFiltered(String title, String author, Pageable pageable);

    @Query("SELECT COUNT(*) FROM book WHERE (:title IS NULL OR title ILIKE '%' || :title || '%') AND (:author IS NULL OR author ILIKE '%' || :author || '%')")
    Mono<Long> countFiltered(String title, String author);
}