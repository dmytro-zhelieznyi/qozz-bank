package io.qozz.qozzbank.messaging.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransferCreatedEvent(
        UUID correlationId,
        UUID operationId,
        UUID fromAccount,
        UUID toAccount,
        BigDecimal amount,
        OffsetDateTime createdAt
) implements RabbitMQEvent {
}
