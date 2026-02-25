package com.example.bookcatalog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;

@Table("books")
public class Book {

    @Id
    private Long id;

    @NotBlank(message = "Title must not be blank")
    private String title;
    private String author;
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    public Book() {}

    public Book(Long id, String title, String author, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public Book(String title, String author, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public BigDecimal getPrice() { return price; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPrice(BigDecimal price) { this.price = price; }
}