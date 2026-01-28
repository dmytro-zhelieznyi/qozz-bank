package io.qozz.qozzbank.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_TRANSFER = "qozzbank.exchange";
    public static final String QUEUE_TRANSFER = "qozzbank.queue.transfers.local";
    public static final String ROUTING_KEY_TRANSFER_CREATED = "key.transfer.created";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_TRANSFER);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_TRANSFER, true, false, false);
    }

    @Bean
    public Binding binding(Queue localQueue, DirectExchange localExchange) {
        return BindingBuilder.bind(localQueue)
                .to(localExchange)
                .with(ROUTING_KEY_TRANSFER_CREATED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
