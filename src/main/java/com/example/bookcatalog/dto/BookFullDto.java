package com.example.bookcatalog.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookFullDto {

    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private LocalDate publishDate;

    public BookFullDto() {
    }

    public BookFullDto(Long id, String title, String author, BigDecimal price, LocalDate publishDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.publishDate = publishDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }
}