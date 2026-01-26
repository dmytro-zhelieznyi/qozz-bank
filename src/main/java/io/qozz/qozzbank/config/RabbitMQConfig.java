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
    @Profile({"local", "test"})
    public DirectExchange localExchange() {
        return new DirectExchange(EXCHANGE_TRANSFER);
    }

    @Bean
    @Profile({"local", "test"})
    public Queue localQueue() {
        return new Queue(QUEUE_TRANSFER, true, false, false);
    }

    @Bean
    @Profile({"local", "test"})
    public Binding localBinding(Queue localQueue, DirectExchange localExchange) {
        return BindingBuilder.bind(localQueue)
                .to(localExchange)
                .with(ROUTING_KEY_TRANSFER_CREATED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
