package com.example.bookcatalog;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class BookcatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookcatalogApplication.class, args);
	}
}