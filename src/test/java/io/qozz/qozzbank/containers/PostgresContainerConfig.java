package io.qozz.qozzbank.containers;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public interface PostgresContainerConfig {
    @Container
    @ServiceConnection
    PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.1")
            .withDatabaseName("qozzbank")
            .withUsername("postgres")
            .withPassword("postgres")
            .withReuse(false);
}
