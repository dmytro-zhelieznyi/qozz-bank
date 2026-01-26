package io.qozz.qozzbank.messaging.event;

import java.util.UUID;

public interface RabbitMQEvent {
    UUID correlationId();
}
