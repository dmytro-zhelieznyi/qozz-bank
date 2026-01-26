package io.qozz.qozzbank.containers;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;

public interface RabbitContainerConfig {
    @Container
    @ServiceConnection
    RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:4.2.2-management")
            .withAdminUser("guest")
            .withAdminPassword("guest")
            .withReuse(false);
}
