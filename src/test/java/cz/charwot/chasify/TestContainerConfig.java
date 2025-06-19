package cz.charwot.chasify;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.testcontainers.*;

@Testcontainers
public class TestContainerConfig {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("chasify_test")
            .withUsername("test_user")
            .withPassword("test_pass");
    
    public static void configureSystemProperties() {
        System.setProperty("DB_URL", postgres.getJdbcUrl());
        System.setProperty("DB_USER", postgres.getUsername());
        System.setProperty("DB_PASSWORD", postgres.getPassword());
        System.setProperty("DB_DRIVER", postgres.getDriverClassName());
    }
}
