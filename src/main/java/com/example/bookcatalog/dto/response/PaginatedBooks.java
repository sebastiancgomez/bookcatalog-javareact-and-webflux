package com.example.bookcatalog.dto.response;

import com.example.bookcatalog.dto.BookDto;
import java.util.List;

public class PaginatedBooks {

    private final PageResponse<?> pageResponse;

    public PaginatedBooks(Long total, List<?> books) {
        this.pageResponse = new PageResponse<>(
                books,
                total,
                0,      // default page (since old class didn’t support it)
                books.size()
        );
    }

    public Long getTotal() {
        return pageResponse.totalElements();
    }

    public List<?> getBooks() {
        return pageResponse.content();
    }

    public PageResponse<?> toPageResponse() {
        return pageResponse;
    }
}