package io.qozz.qozzbank.messaging.handler;

import io.qozz.qozzbank.messaging.RabbitMQEventPublisher;
import io.qozz.qozzbank.messaging.event.TransferCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionalTransferEventHandler {
    private final RabbitMQEventPublisher rabbitMQEventPublisher;

    @Async
    // TODO: Implement Transactional Outbox pattern to ensure atomicity between DB updates and event publishing.
    //  Store events in 'outbox' table (status: NEW/SENT) within the same local transaction to avoid dual-write consistency issues.
    @Retryable(
            includes = Exception.class,
            maxRetries = 3,
            delay = 2000
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TransferCreatedEvent event) {
        log.info("[{}] [EVENT_HANDLER_START] Transaction committed. Publishing to RabbitMQ. OperationId: [{}]",
                event.correlationId(), event.operationId());

        try {
            rabbitMQEventPublisher.publish(event);

            log.info("[{}] [EVENT_HANDLER_SUCCESS] Message successfully sent to exchange",
                    event.correlationId());
        } catch (Exception e) {
            log.error("[{}] [EVENT_HANDLER_ERROR] Failed to publish event. Error: [{}]",
                    event.correlationId(), e.getMessage());
        }
    }
}