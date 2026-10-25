package com.yankardev.sitrac.combustible.solicitud;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitCombustibleConfig {

    public static final String EXCHANGE = "sitrac.events";
    public static final String QUEUE = "sitrac.combustible";

    @Bean
    TopicExchange combustibleExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue combustibleQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    Binding programacionCreadaBinding(Queue combustibleQueue, TopicExchange combustibleExchange) {
        return BindingBuilder.bind(combustibleQueue)
                .to(combustibleExchange)
                .with("programacion.creada");
    }

    @Bean
    MessageConverter combustibleRabbitJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
