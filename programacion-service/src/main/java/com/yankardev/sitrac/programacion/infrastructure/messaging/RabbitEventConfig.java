package com.yankardev.sitrac.programacion.infrastructure.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitEventConfig {

    public static final String EXCHANGE = "sitrac.events";

    @Bean
    TopicExchange sitracExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    MessageConverter rabbitJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
