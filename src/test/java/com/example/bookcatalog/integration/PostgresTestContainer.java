package com.example.bookcatalog.integration;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer {

    private static final PostgreSQLContainer<?> INSTANCE =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withInitScript("schema.sql");

    static {
        INSTANCE.start();
    }

    public static PostgreSQLContainer<?> getInstance() {
        return INSTANCE;
    }
}