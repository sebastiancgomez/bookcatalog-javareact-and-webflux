package com.example.bookcatalog.repository;

import com.example.bookcatalog.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface BookRepository extends ReactiveCrudRepository<Book, Long> {

    @Query("SELECT * FROM book " +
            "WHERE (:title IS NULL OR title ILIKE '%' || :title || '%') " +
                "AND (:author IS NULL OR author ILIKE '%' || :author || '%') " +
                "AND (:from IS NULL OR  publish_date >= :from) " +
                "AND (:to IS NULL OR  publish_date <= :to) " +
            "ORDER BY id LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}")
    Flux<Book> findFiltered(String title, String author, LocalDate from, LocalDate to, Pageable pageable);

    @Query("SELECT COUNT(*) FROM book " +
            "WHERE (:title IS NULL OR title ILIKE '%' || :title || '%') " +
                "AND (:author IS NULL OR author ILIKE '%' || :author || '%') "+
                "AND (:from IS NULL OR  publish_date >= :from) " +
                "AND (:to IS NULL OR  publish_date <= :to) " )
    Mono<Long> countFiltered(String title, String author, LocalDate from, LocalDate to);
}