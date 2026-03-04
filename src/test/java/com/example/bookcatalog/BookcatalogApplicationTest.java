package com.example.bookcatalog;

import com.example.bookcatalog.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BookcatalogApplicationTest {

    @Test
    void contextLoads() {
    }
}