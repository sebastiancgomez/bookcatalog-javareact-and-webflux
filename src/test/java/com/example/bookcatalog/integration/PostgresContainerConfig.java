package com.example.bookcatalog.integration;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class PostgresContainerConfig {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("bookcatalog")
                    .withUsername("postgres")
                    .withPassword("postgres");

    static {
        postgres.start();

        System.setProperty("spring.r2dbc.url",
                "r2dbc:postgresql://"
                        + postgres.getHost()
                        + ":"
                        + postgres.getFirstMappedPort()
                        + "/bookcatalog");

        System.setProperty("spring.r2dbc.username", "postgres");
        System.setProperty("spring.r2dbc.password", "postgres");

        System.setProperty("spring.flyway.url", postgres.getJdbcUrl());
        System.setProperty("spring.flyway.user", "postgres");
        System.setProperty("spring.flyway.password", "postgres");
    }
}