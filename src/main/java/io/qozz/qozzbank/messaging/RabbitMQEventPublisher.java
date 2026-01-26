package io.qozz.qozzbank.messaging;


import io.qozz.qozzbank.messaging.event.RabbitMQEvent;
import io.qozz.qozzbank.messaging.event.TransferCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQEventPublisher {
    private static final String EXCHANGE_TRANSFER = "qozzbank.exchange";
    private static final String QUEUE_TRANSFER = "qozzbank.queue.transfers.local";
    private static final String ROUTING_KEY_TRANSFER_CREATED = "key.transfer.created";

    private final RabbitTemplate rabbitTemplate;

    private void publish(String exchange, String routingKey, RabbitMQEvent event) {
        log.info("[{}] [RABBIT_PUBLISH] Sending to Exchange: [{}], RoutingKey: [{}]", event.correlationId(), exchange, routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

    public void publish(TransferCreatedEvent event) {
        publish(EXCHANGE_TRANSFER, ROUTING_KEY_TRANSFER_CREATED, event);
    }
}
